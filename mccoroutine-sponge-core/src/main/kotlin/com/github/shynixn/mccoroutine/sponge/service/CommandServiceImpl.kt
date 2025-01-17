package com.github.shynixn.mccoroutine.sponge.service

import com.github.shynixn.mccoroutine.sponge.SuspendingCommandExecutor
import com.github.shynixn.mccoroutine.sponge.launch
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandExecutor
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.event.lifecycle.LoadedGameEvent
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent
import org.spongepowered.plugin.PluginContainer

internal class CommandServiceImpl(private val plugin: PluginContainer) {
    /**
     * Registers a suspend command executor.
     */
    fun registerSuspendCommandExecutor(
        event: RegisterCommandEvent<Command.Parameterized>,
        alias: String,
        command: Command.Builder,
        commandExecutor: SuspendingCommandExecutor
    ) {
        command.executor { ctx ->
            var commandResult = CommandResult.success();

            // Commands in sponge always arrive synchronously. Therefore, we can simply use the default properties.
            plugin.launch {
                commandResult = commandExecutor.execute(ctx)
            }

            commandResult
        }

        event.register(plugin, command.build(), alias)
    }
}
