package com.github.shynixn.mccoroutine.velocity

import com.google.inject.Inject
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger

/**
 * When injecting this class into your plugin instance, a new coroutine session is booted.
 * Calling initialize allows to listen to suspend events in your plugin main class.
 */
@Suppress("ConvertSecondaryConstructorToPrimary")
class SuspendingPluginContainer
@Inject constructor(
    val pluginContainer: PluginContainer,
    val server: ProxyServer,
    val logger: Logger
) {

    /**
     * Initializes the MCCoroutine hook into the plugin.
     */
    init {
        mcCoroutine.disableLogging(pluginContainer, this)
    }

    /**
     * Needs to be called to listen to suspend events in your plugin main class.
     */
    fun initialize(pluginInstance: Any) {
        mcCoroutine.setupCoroutineSession(pluginInstance, pluginContainer, this)
        mcCoroutine.getCoroutineSession(pluginInstance).registerSuspendListener(pluginInstance, true)
    }
}
