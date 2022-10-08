package com.github.shynixn.mccoroutine.sponge

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.spongepowered.api.command.*
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.exception.ArgumentParseException
import org.spongepowered.api.command.parameter.ArgumentReader
import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.command.parameter.Parameter
import org.spongepowered.api.command.parameter.managed.Flag
import org.spongepowered.api.text.Text
import org.spongepowered.plugin.PluginContainer
import java.util.*
import java.util.function.Predicate

abstract class SuspendingCommandParameter(pluginContainer: PluginContainer, text: Component) {
    private val commandElement =object : Command.Parameterized {
        /**
         * Attempt to extract a value for this element from the given arguments.
         * This method is expected to have no side-effects for the source, meaning
         * that executing it will not change the state of the [CommandSource]
         * in any way.
         *
         * @param source The source to parse for
         * @param args the arguments
         * @return The extracted value
         * @throws ArgumentParseException if unable to extract a value
         */
        override fun parseValue(source: CommandSource, args: CommandArgs): Any? {
            var parsedValue: Any? = null
            // Sponge uses Exceptions for StateHandling which is a terrible thing to do.
            // Therefore, we need to manually move the exception from the coroutine scope to the caller scope.
            var exception: Throwable? = null

            pluginContainer.launch {
                try {
                    parsedValue = this@SuspendingCommandParameter.parseValue(source, args)
                } catch (e: Throwable) {
                    exception = e
                }
            }

            if (exception != null) {
                throw exception!!
            }

            return parsedValue
        }

        /**
         * Fetch completions for command arguments.
         *
         * @param src The source requesting tab completions
         * @param args The arguments currently provided
         * @param context The context to store state in
         * @return Any relevant completions
         */
        override fun complete(src: CommandSource, args: CommandArgs, context: CommandContext): List<String?>? {
            var parsedValue: List<String?>? = null
            // Sponge uses Exceptions for StateHandling which is a terrible thing to do.
            // Therefore, we need to manually move the exception from the coroutine scope to the caller scope.
            var exception: Throwable? = null

            pluginContainer.launch {
                try {
                    parsedValue = this@SuspendingCommandParameter.complete(src, args, context)
                } catch (e: Throwable) {
                    exception = e
                }
            }

            if (exception != null) {
                throw exception!!
            }

            return parsedValue
        }

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

        override fun usage(cause: CommandCause?): Component = Component.text("<${PlainTextComponentSerializer.plainText().serialize(text)}>")

        override fun flags(): MutableList<Flag> = mutableListOf()

        override fun parameters(): MutableList<Parameter> = mutableListOf()

        override fun subcommands(): MutableList<Parameter.Subcommand> = mutableListOf()

        override fun isTerminal(): Boolean = true

        override fun executionRequirements(): Predicate<CommandCause> = Predicate { true }

        override fun parseArguments(cause: CommandCause, arguments: ArgumentReader.Mutable): CommandContext {
            var parsedValue: CommandContext? = null
            // Sponge uses Exceptions for StateHandling which is a terrible thing to do.
            // Therefore, we need to manually move the exception from the coroutine scope to the caller scope.
            var exception: Throwable? = null

            pluginContainer.launch {
                try {
                    parsedValue = this@SuspendingCommandParameter.parseValue(source, args)
                } catch (e: Throwable) {
                    exception = e
                }
            }

            if (exception != null) {
                throw exception!!
            }

            return parsedValue ?: CommandContext.Builder
        }

        override fun executor(): Optional<CommandExecutor> = Optional.empty()
    }

    /**
     * Converts this [SuspendingCommandParameter] to a Sponge-Api compatible [CommandElement].
     */
    fun toParameter(): Parameter.Subcommand {
        return commandElement
    }

    /**
     * Return the key to be used for this object.
     *
     * @return the user-facing representation of the key
     */
    open fun getKey(): Component? {
        return Component.text(commandElement.aliases().first())
    }

    /**
     * Return the plain key, to be used when looking up this command element in
     * a [CommandContext]. If the key is a [TranslatableText], this
     * is the translation's id. Otherwise, this is the result of
     * [Text.toPlain].
     *
     * @return the raw key
     */
    open fun getUntranslatedKey(): String? {
        return commandElement.untranslatedKey
    }

    /**
     * Attempt to extract a value for this element from the given arguments and
     * put it in the given context. This method normally delegates to
     * [.parseValue] for getting the values.
     * This method is expected to have no side-effects for the source, meaning
     * that executing it will not change the state of the [CommandSource]
     * in any way.
     *
     * @param source The source to parse for
     * @param args The args to extract from
     * @param context The context to supply to
     * @throws ArgumentParseException if unable to extract a value
     */
    @Throws(ArgumentParseException::class)
    open fun parse(source: CommandSource, args: CommandArgs, context: CommandContext) {
        return commandElement.parse(source, args, context)
    }

    /**
     * Attempt to extract a value for this element from the given arguments.
     * This method is expected to have no side-effects for the source, meaning
     * that executing it will not change the state of the [CommandSource]
     * in any way.
     *
     * @param source The source to parse for
     * @param args the arguments
     * @return The extracted value
     * @throws ArgumentParseException if unable to extract a value
     */
    @Throws(ArgumentParseException::class)
    protected abstract suspend fun parseValue(source: CommandSource, args: CommandArgs): Any?

    /**
     * Fetch completions for command arguments.
     *
     * @param src The source requesting tab completions
     * @param args The arguments currently provided
     * @param context The context to store state in
     * @return Any relevant completions
     */
    abstract suspend fun complete(ctx: CommandContext): List<String?>?

    /**
     * Return a usage message for this specific argument.
     *
     * @param src The source requesting usage
     * @return The formatted usage
     */
    open fun getUsage(src: CommandSource): Text? {
        return commandElement.getUsage(src)
    }
}
