package com.github.shynixn.mccoroutine.sponge.extension

import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import org.spongepowered.api.Sponge
import org.spongepowered.api.scheduler.ScheduledTask
import org.spongepowered.api.scheduler.Task
import org.spongepowered.plugin.PluginContainer
import java.lang.reflect.Method

/**
 * Internal reflection suspend.
 */
internal suspend fun Method.invokeSuspend(obj: Any, vararg args: Any?): Any? =
    suspendCoroutineUninterceptedOrReturn { cont ->
        invoke(obj, *args, cont)
    }

/**
 * Gets if the plugin is still enabled.
 */
internal val PluginContainer.isEnabled: Boolean
    get() {
        return Sponge.server().game().pluginManager().plugins().contains(this)
    }

/**
 * Runs task
 */
internal fun Task.Builder.submit(plugin: PluginContainer, async: Boolean = false): ScheduledTask {
    val task = plugin(plugin).build()
    return if (async) {
        Sponge.asyncScheduler().submit(task)
    } else {
        Sponge.server().scheduler().submit(task)
    }
}