package com.github.shynixn.mccoroutine.sponge.sample.impl

import com.github.shynixn.mccoroutine.sponge.asyncDispatcher
import com.github.shynixn.mccoroutine.sponge.sample.entity.UserData
import com.github.shynixn.mccoroutine.sponge.scope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.plugin.PluginContainer
import java.util.concurrent.CompletionStage

class UserDataCache(private val plugin: PluginContainer, private val fakeDatabase: FakeDatabase) {
    private val cache = HashMap<Player, Deferred<UserData>>()

    /**
     * Clears the player cache.
     */
    fun clearCache(player: Player) {
        cache.remove(player)
    }

    /**
     * Saves the cached data of the player.
     */
    suspend fun saveUserData(player: Player) {
        val userData = cache[player]!!.await()
        withContext(plugin.asyncDispatcher) {
            fakeDatabase.saveUserData(userData)
        }
    }

    /**
     * Gets the user data from the player.
     */
    suspend fun getUserDataFromPlayerAsync(player: Player): Deferred<UserData> {
        return coroutineScope {
            if (!cache.containsKey(player)) {
                cache[player] = async(plugin.asyncDispatcher) {
                    println("[Cache] is downloading async: " + !Sponge.getServer().isMainThread)
                    fakeDatabase.getUserDataFromPlayer(player)
                }
            }
            println("[Cache] is downloading waiting on Primary Thread: " + Sponge.getServer().isMainThread)
            cache[player]!!
        }
    }

    /**
     * Gets the user data from the player.
     *
     * This method is only useful if you plan to access suspend functions from Java. It
     * is not possible to call suspend functions directly from java, so we need to
     * wrap it into a Java 8 CompletionStage.
     *
     * This might be useful if you plan to provide a Developer Api for your plugin as other
     * plugins may be written in Java or if you have got Java code in your plugin.
     */
    fun getUserDataFromPlayer(player: Player): CompletionStage<UserData> {
        return plugin.scope.future {
            getUserDataFromPlayerAsync(player).await()
        }
    }
}
