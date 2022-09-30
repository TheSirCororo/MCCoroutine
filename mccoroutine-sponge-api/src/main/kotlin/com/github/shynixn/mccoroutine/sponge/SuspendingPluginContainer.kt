package com.github.shynixn.mccoroutine.sponge

import com.google.inject.Inject
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.Command
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.lifecycle.LoadedGameEvent
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent
import org.spongepowered.plugin.PluginContainer

/**
 * Commands for all containers
 */
val commands = mutableMapOf<PluginContainer, MutableMap<String, Command.Parameterized>>()

/**
 * When injecting this class into one instance of your plugin, the instance
 * of your plugin automatically becomes a suspending listener, so you can
 * append suspend to any of your startup methods.
 */
class SuspendingPluginContainer {
    @Inject
    private lateinit var internalContainer: PluginContainer

    /**
     * Gets the plugin container.
     */
    val pluginContainer: PluginContainer
        get() {
            return internalContainer
        }

    /**
     * Registers this instance as a listener.
     */
    @Inject
    fun setContainer(pluginContainer: PluginContainer) {
        Sponge.eventManager().registerListeners(pluginContainer, this)
    }

    /**
     * At the earliest possible moment at the earliest game construction
     * event the plugin instance is swapped with a suspending listener.
     */
    @Listener
    fun onGameInitializeEvent(event: LoadedGameEvent) {
        val instance = internalContainer.instance()
        Sponge.eventManager().unregisterListeners(instance)
        Sponge.eventManager().registerSuspendingListeners(internalContainer, instance)
    }

    @Listener
    fun registerCommands(event: RegisterCommandEvent<Command.Parameterized>) {
        commands[internalContainer]?.forEach { entry ->
            event.register(internalContainer, entry.value, entry.key)
        }
    }
}
