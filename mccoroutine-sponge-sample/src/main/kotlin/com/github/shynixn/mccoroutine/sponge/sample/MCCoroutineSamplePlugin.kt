package com.github.shynixn.mccoroutine.sponge.sample

import com.github.shynixn.mccoroutine.sponge.*
import com.github.shynixn.mccoroutine.sponge.sample.commandexecutor.AdminCommandExecutor
import com.github.shynixn.mccoroutine.sponge.sample.impl.FakeDatabase
import com.github.shynixn.mccoroutine.sponge.sample.impl.UserDataCache
import com.github.shynixn.mccoroutine.sponge.sample.listener.EntityInteractListener
import com.github.shynixn.mccoroutine.sponge.sample.listener.PlayerConnectListener
import com.google.inject.Inject
import jdk.javadoc.internal.doclets.toolkit.taglets.ParamTaglet
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandExecutor
import org.spongepowered.api.command.parameter.Parameter
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.lifecycle.LoadedGameEvent
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent
import org.spongepowered.plugin.PluginContainer
import org.spongepowered.plugin.Plugin

@Suppress("unused")
@Plugin(
    id = "mccoroutinesample",
    name = "MCCoroutineSample",
    description = "MCCoroutineSample is sample plugin to use MCCoroutine in Sponge."
)
class MCCoroutineSamplePlugin {
    @Inject
    private lateinit var plugin: PluginContainer

    @Inject
    private lateinit var suspendingPluginContainer: SuspendingPluginContainer
    val database = FakeDatabase()
    val cache = UserDataCache(plugin, database)

    /**
     * OnEnable.
     */
    @Listener
    suspend fun onEnable(event: LoadedGameEvent) {

        println("[MCCoroutineSamplePlugin] OnEnable on Primary Thread: " + Sponge.server().onMainThread())

        withContext(plugin.asyncDispatcher) {
            println("[MCCoroutineSamplePlugin] Loading some data on async Thread: " + Sponge.server().onMainThread())
            Thread.sleep(500)
        }

        // Extension to traditional registration.
        Sponge.eventManager().registerSuspendingListeners(plugin, PlayerConnectListener(plugin, cache))
        Sponge.eventManager().registerSuspendingListeners(plugin, EntityInteractListener(cache))
        val commandSpec = Command.builder()
            .shortDescription(Component.text("Command for operations."))
            .permission("mccoroutine.sample")
            .addParameters(
                GenericArguments.onlyOne(
                    AdminCommandExecutor.SetCommandParameter(plugin, Text.of("action")).toCommandElement()
                ),
                GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                GenericArguments.onlyOne(GenericArguments.integer(Text.of("kills")))
            )
            .ex(plugin, AdminCommandExecutor(cache, plugin))
        Sponge.server().commandManager().registrar(CommandExecutor::class.java).get().plugin, commandSpec.build(), listOf("mccor"))
    }

    @Listener
    fun onCommand(event: RegisterCommandEvent<Command.Parameterized>) {
        val commandBuilder = Command.builder()
            .shortDescription(Component.text("Command for operations."))
            .permission("mccoroutine.sample")
            .addParameters(
                Parameter.subcommand(
                    AdminCommandExecutor.SetCommandParameter(plugin, Text.of("action")).toParameter()
                ),
                Parameter.player().key("player").build(),
                Parameter.integerNumber().key("kills").build()
            )

        event.registerSuspendingExecutor("mccor", plugin, commandBuilder, AdminCommandExecutor(cache, database))
    }
}
