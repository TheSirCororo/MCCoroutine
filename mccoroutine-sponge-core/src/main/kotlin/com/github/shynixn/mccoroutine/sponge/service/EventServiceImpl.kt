package com.github.shynixn.mccoroutine.sponge.service

import com.github.shynixn.mccoroutine.sponge.EventExecutionType
import com.github.shynixn.mccoroutine.sponge.asyncDispatcher
import com.github.shynixn.mccoroutine.sponge.minecraftDispatcher
import com.github.shynixn.mccoroutine.sponge.extension.invokeSuspend
import com.github.shynixn.mccoroutine.sponge.launch
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.EventListener
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.plugin.PluginContainer
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.util.logging.Level
import java.util.logging.Logger

private val spongeEventManagerClazz: Class<*> = Class.forName("org.spongepowered.common.event.manager.SpongeEventManager")
private val registeredListenersField: Field = spongeEventManagerClazz.getDeclaredField("registeredListeners").apply {
    isAccessible = true
}
private val registerHandleMethod: Method = spongeEventManagerClazz.getDeclaredMethod("register", List::class.java).apply {
    isAccessible = true
}
private val handlerCacheMethod: Method = spongeEventManagerClazz.getDeclaredMethod("getHandlerCache", Event::class.java).apply {
    isAccessible = true
}
private val listenerClazz: Class<*> = Class.forName("org.spongepowered.common.event.manager.RegisteredListener")
private val listenerField: Field = listenerClazz.getDeclaredField("listener").apply {
    isAccessible = true
}
private val cacheAccess: Class<*> = Class.forName("org.spongepowered.common.event.manager.RegisteredListener\$Cache")
private val cacheGetListenersMethod: Method = cacheAccess.getDeclaredMethod("getListeners")
private val createRegistrationMethod =
    spongeEventManagerClazz.getDeclaredMethod(
        "createRegistration",
        PluginContainer::class.java,
        Type::class.java,
        Order::class.java,
        Boolean::class.java,
        EventListener::class.java
    ).apply {
        isAccessible = true
    }

internal class EventServiceImpl(private val plugin: PluginContainer, private val logger: Logger) {
    /**
     * Registers a suspend listener.
     */
    @Suppress("UNCHECKED_CAST")
    fun registerSuspendListener(listener: Any) {
        val registeredListeners = registeredListenersField.get(Sponge.eventManager()) as MutableSet<Any>

        if (registeredListeners.contains(listener)) {
            try {
                throw Exception("Stack trace")
            } catch (e: Exception) {
                val writer = StringWriter()
                e.printStackTrace(PrintWriter(writer))
                val data = writer.toString()
                // When using the suspending PluginContainer a false positiv event might be thrown. We can safely ignore that.
                if (!data.contains("com.github.shynixn.mccoroutine.sponge.SuspendingPluginContainer.onGameInitializeEvent")) {
                    this.logger.log(
                        Level.SEVERE,
                        "Plugin ${plugin.metadata().id()} attempted to register an already registered listener ({${listener::class.java.name}})"
                    )

                    Thread.dumpStack()
                    return
                }

                registeredListeners.remove(listener)
            }
        }

        val handlers = ArrayList<Any>()

        for (method in listener.javaClass.methods) {
            val listenerAnnotation = method.getAnnotation(Listener::class.java) ?: continue

            val eventType: Any = method.genericParameterTypes[0]

            try {
                // Using the AnnotatedEventListener.Factory will not work because of Filter annotations.
                method.isAccessible = true
                val handler = MCCoroutineEventListener(listener, method, plugin)

                val registration = createRegistrationMethod.invoke(
                    Sponge.eventManager(),
                    plugin,
                    eventType,
                    listenerAnnotation.order,
                    listenerAnnotation.beforeModifications,
                    handler
                )
                handlers.add(registration)
            } catch (e: Exception) {
                throw IllegalArgumentException("Failed to create handler for {${method.name}} on {${listener}}", e)
            }
        }

        registeredListeners.add(listener)
        registerHandleMethod.invoke(Sponge.eventManager(), handlers)
    }

    /**
     * Fires a suspending [event] with the given [eventExecutionType].
     * @return Collection of receiver jobs. May already be completed.
     */
    fun fireSuspendingEvent(event: Event, eventExecutionType: EventExecutionType): Collection<Job> {
        val listenerCache = handlerCacheMethod.invoke(Sponge.eventManager(), event)
        val listeners = cacheGetListenersMethod.invoke(listenerCache) as List<Any>
        val jobs = ArrayList<Job>()
        if (eventExecutionType == EventExecutionType.Concurrent) {
            for (listener in listeners) {
                val eventListener = listenerField.get(listener) as EventListener<Event>

                try {
                    if (eventListener is MCCoroutineEventListener) {
                        val job = eventListener.handleSuspend(event)
                        jobs.add(job)
                    } else {
                        eventListener.handle(event)
                    }
                } catch (e: Throwable) {
                    this.logger.log(
                        Level.SEVERE,
                        "Could not pass {${event.javaClass.simpleName}} to {${plugin.metadata().name().get()}}.", e
                    )
                }
            }
        } else {
            jobs.add(plugin.launch(Dispatchers.Unconfined) {
                for (listener in listeners) {
                    val eventListener = listenerField.get(listener) as EventListener<Event>

                    try {
                        if (eventListener is MCCoroutineEventListener) {
                            eventListener.handleSuspend(event).join()
                        } else {
                            eventListener.handle(event)
                        }
                    } catch (e: Throwable) {
                        logger.log(
                            Level.SEVERE,
                            "Could not pass {${event.javaClass.simpleName}} to {${plugin.metadata().name()}}.", e
                        )
                    }
                }
            })
        }

        return jobs
    }

    private class MCCoroutineEventListener(
        private val listener: Any,
        private val method: Method,
        private val plugin: PluginContainer
    ) :
        EventListener<Event> {

        /**
         * Called when a [Event] registered to this listener is called.
         *
         * @param event The called event
         * @throws Exception If an error occurs
         */
        fun handleSuspend(event: Event): Job {
            return handleEvent(event)
        }

        /**
         * Called when a [Event] registered to this listener is called.
         *
         * @param event The called event
         * @throws Exception If an error occurs
         */
        override fun handle(event: Event) {
            handleEvent(event)
        }

        /**
         * Called when a [Event] registered to this listener is called.
         *
         * @param event The called event
         * @throws Exception If an error occurs
         */
        private fun handleEvent(event: Event): Job {
            val dispatcher = if (!Sponge.server().onMainThread()) {
                // Unconfined because async events should be supported too.
                plugin.asyncDispatcher
            } else {
                plugin.minecraftDispatcher
            }

            return plugin.launch(dispatcher, CoroutineStart.UNDISPATCHED) {
                try {
                    // Try as suspension function.
                    method.invokeSuspend(listener, event)
                } catch (e: IllegalArgumentException) {
                    // Try as ordinary function.
                    method.invoke(listener, event)
                }
            }
        }
    }
}
