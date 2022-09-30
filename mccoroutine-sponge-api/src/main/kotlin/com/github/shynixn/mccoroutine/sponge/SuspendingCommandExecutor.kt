package com.github.shynixn.mccoroutine.sponge

import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.CommandContext

/**
 * Interface containing the method directing how a certain command will
 * be executed.
 */
@FunctionalInterface
interface SuspendingCommandExecutor {
    /**
     * Callback for the execution of a command.
     *
     * @param ctx The context of the command
     * @return the result of executing this command.
     */
    suspend fun execute(ctx: CommandContext): CommandResult
}

