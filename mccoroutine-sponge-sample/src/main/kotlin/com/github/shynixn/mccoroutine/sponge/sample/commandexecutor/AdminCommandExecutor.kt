package com.github.shynixn.mccoroutine.sponge.sample.commandexecutor

import com.github.shynixn.mccoroutine.sponge.SuspendingCommandParameter
import com.github.shynixn.mccoroutine.sponge.SuspendingCommandExecutor
import com.github.shynixn.mccoroutine.sponge.getOne
import com.github.shynixn.mccoroutine.sponge.postSuspending
import com.github.shynixn.mccoroutine.sponge.sample.impl.UserDataCache
import kotlinx.coroutines.joinAll
import net.kyori.adventure.text.Component
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.command.CommandCompletion
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.ArgumentReader
import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Cause
import org.spongepowered.api.event.impl.AbstractEvent
import org.spongepowered.plugin.PluginContainer

class AdminCommandExecutor(private val userDataCache: UserDataCache, private val pluginContainer: PluginContainer) :
    SuspendingCommandExecutor {
    /**
     * Callback for the execution of a command.
     *
     * @param ctx The context of this command
     * @return the result of executing this command.
     */
    override suspend fun execute(ctx: CommandContext): CommandResult {
        val player = ctx.getOne<Player>("player").get()
        val playerKills = ctx.getOne<Int>("kills").get()

        println("[AdminCommandExecutor] Is starting on Primary Thread: " + Sponge.server().onMainThread())
        val userData = userDataCache.getUserDataFromPlayerAsync(player).await()
        userData.amountOfPlayerKills = playerKills
        userDataCache.saveUserData(player)
        println("[AdminCommandExecutor] Is ending on Primary Thread: " + Sponge.server().onMainThread())

        println("[AdminCommandExecutor] Is starting on Primary Thread: " + Sponge.server().onMainThread())
        val event = MCCoroutineEvent()
        Sponge.eventManager().postSuspending(event, pluginContainer).joinAll()
        println("[AdminCommandExecutor] Is ending on Primary Thread: " + Sponge.server().onMainThread())

        return CommandResult.success()
    }

    class MCCoroutineEvent : AbstractEvent() {
        /**
         * Gets the cause for the event.
         *
         * @return The cause
         */
        override fun cause(): Cause? {
            return null
        }
    }

    class SetCommandParameter(pluginContainer: PluginContainer, text: Component) :
        SuspendingCommandParameter(pluginContainer, text) {

        /**
         * Fetch completions for command arguments.
         *
         * @param cause The source requesting tab completions
         * @param args The arguments currently provided
         * @return Any relevant completions
         */
        override suspend fun complete(
            cause: CommandCause,
            args: ArgumentReader.Mutable
        ): MutableList<CommandCompletion> {
            return mutableListOf(CommandCompletion.of("set"))
        }
    }
}
