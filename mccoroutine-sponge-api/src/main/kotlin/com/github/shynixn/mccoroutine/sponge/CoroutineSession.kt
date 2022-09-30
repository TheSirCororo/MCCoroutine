package com.github.shynixn.mccoroutine.sponge

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.spongepowered.api.command.Command
import org.spongepowered.api.event.Event
import kotlin.coroutines.CoroutineContext

/**
 * Facade of a coroutine session of a single plugin.
 */
interface CoroutineSession {
    /**
     * Plugin scope.
     */
    val scope: CoroutineScope

    /**
     * Minecraft Dispatcher.
     */
    val dispatcherMinecraft: CoroutineContext

    /**
     * Async Dispatcher.
     */
    val dispatcherAsync: CoroutineContext

    /**
     * Registers a suspend command executor.
     */
    fun registerSuspendCommandExecutor(alias: String, command: Command.Builder = Command.builder(), commandExecutor: SuspendingCommandExecutor)

    /**
     * Registers a suspend listener.
     */
    fun registerSuspendListener(listener: Any)

    /**
     * Fires a suspending [event] with the given [eventExecutionType].
     * @return Collection of receiver jobs. May already be completed.
     */
    fun fireSuspendingEvent(event: Event, eventExecutionType: EventExecutionType): Collection<Job>
}
