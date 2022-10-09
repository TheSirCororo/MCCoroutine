package com.github.shynixn.mccoroutine.sponge

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.spongepowered.api.command.*
import org.spongepowered.api.command.exception.ArgumentParseException
import org.spongepowered.api.command.parameter.ArgumentReader
import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.command.parameter.Parameter
import org.spongepowered.api.command.parameter.managed.Flag
import org.spongepowered.plugin.PluginContainer
import java.util.*
import java.util.function.Predicate

abstract class SuspendingCommandParameter(pluginContainer: PluginContainer, text: Component) {
    private val commandElement = object : Command.Parameterized {
        override fun complete(
            cause: CommandCause,
            arguments: ArgumentReader.Mutable
        ): MutableList<CommandCompletion> {
            var parsedValue: MutableList<CommandCompletion> = mutableListOf()
            // Sponge uses Exceptions for StateHandling which is a terrible thing to do.
            // Therefore, we need to manually move the exception from the coroutine scope to the caller scope.
            var exception: Throwable? = null

            pluginContainer.launch {
                try {
                    parsedValue = this@SuspendingCommandParameter.complete(cause, arguments)
                } catch (e: Throwable) {
                    exception = e
                }
            }

            if (exception != null) {
                throw exception!!
            }

            return parsedValue
        }

        override fun canExecute(cause: CommandCause?): Boolean = true

        override fun shortDescription(cause: CommandCause?): Optional<Component> = Optional.empty()

        override fun extendedDescription(cause: CommandCause?): Optional<Component> = Optional.empty()

        override fun usage(cause: CommandCause): Component = this@SuspendingCommandParameter.getUsage(cause)

        override fun flags(): MutableList<Flag> = mutableListOf()

        override fun parameters(): MutableList<Parameter> = mutableListOf()

        override fun subcommands(): MutableList<Parameter.Subcommand> = mutableListOf()

        override fun isTerminal(): Boolean = true

        override fun executionRequirements(): Predicate<CommandCause> = Predicate { true }

        override fun parseArguments(cause: CommandCause?, arguments: ArgumentReader.Mutable?): CommandContext {
            TODO("Not yet implemented")
        }

        override fun executor(): Optional<CommandExecutor> = Optional.empty()
    }

    /**
     * Converts this [SuspendingCommandParameter] to a Sponge-Api compatible [Command.Parameterized].
     */
    fun toParameter(): Command.Parameterized {
        return commandElement
    }

    /**
     * Fetch completions for command arguments.
     *
     * @param cause The source requesting tab completions
     * @param args The arguments currently provided
     * @return Any relevant completions
     */
    abstract suspend fun complete(cause: CommandCause, args: ArgumentReader.Mutable): MutableList<CommandCompletion>

    /**
     * Return a usage message for this specific argument.
     *
     * @param cause The source requesting usage
     * @return The formatted usage
     */
    open fun getUsage(cause: CommandCause): Component {
        return Component.empty()
    }
}
