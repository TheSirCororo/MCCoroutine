package com.github.shynixn.mccoroutine.sponge.dispatcher

import com.github.shynixn.mccoroutine.sponge.extension.isEnabled
import com.github.shynixn.mccoroutine.sponge.extension.submit
import kotlinx.coroutines.CoroutineDispatcher
import org.spongepowered.api.Sponge
import org.spongepowered.api.scheduler.Task
import org.spongepowered.plugin.PluginContainer
import kotlin.coroutines.CoroutineContext

internal class MinecraftCoroutineDispatcher(private val plugin: PluginContainer) :
    CoroutineDispatcher() {
    /**
     * Returns `true` if the execution of the coroutine should be performed with [dispatch] method.
     * The default behavior for most dispatchers is to return `true`.
     * This method should generally be exception-safe. An exception thrown from this method
     * may leave the coroutines that use this dispatcher in the inconsistent and hard to debug state.
     */
    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return !Sponge.server().onMainThread()
    }

    /**
     * Handles dispatching the coroutine on the correct thread.
     */
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!plugin.isEnabled) {
            return
        }

        Task.builder()
            .execute(block)
            .submit(plugin)
    }
}
