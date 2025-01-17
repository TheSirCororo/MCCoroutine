package com.github.shynixn.mccoroutine.bungeecord.extension

import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import net.md_5.bungee.api.plugin.Plugin
import java.lang.reflect.Method

/**
 * Gets if the plugin is still enabled.
 */
internal val Plugin.isEnabled : Boolean
    get() {
        return !this.executorService.isShutdown
    }

/**
 * Internal reflection suspend.
 */
internal suspend fun Method.invokeSuspend(obj: Any, vararg args: Any?): Any? =
    suspendCoroutineUninterceptedOrReturn { cont ->
        invoke(obj, *args, cont)
    }

