package com.github.shynixn.mccoroutine.velocity.impl

import com.github.shynixn.mccoroutine.velocity.entity.UserData
import com.github.shynixn.mccoroutine.velocity.scope
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap

class UserDataCache(private val plugin: PluginContainer, private val fakeDatabase: FakeDatabase) {
    // ConcurrentHashmap is important because velocity entirely works on multi threading.
    private val cache = ConcurrentHashMap<Player, Deferred<UserData>>()

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
        println("[UserDataCache/saveUserData] Is starting on Thread:${Thread.currentThread().name}/${Thread.currentThread().id}")
        withContext(Dispatchers.IO) {
            fakeDatabase.saveUserData(userData)
            println("[UserDataCache/saveUserData] Is saving on Thread:${Thread.currentThread().name}/${Thread.currentThread().id}")
        }
        println("[UserDataCache/saveUserData] Is ending on Thread:${Thread.currentThread().name}/${Thread.currentThread().id}")
    }

    /**
     * Gets the user data from the player.
     */
    suspend fun getUserDataFromPlayerAsync(player: Player): Deferred<UserData> {
        return coroutineScope {
            if (!cache.containsKey(player)) {
                println("[UserDataCache/getUserDataFromPlayerAsync] Is starting on Thread:${Thread.currentThread().name}/${Thread.currentThread().id}")
                cache[player] = async(Dispatchers.IO) {
                    println("[UserDataCache/getUserDataFromPlayerAsync] Is downloading on Thread:${Thread.currentThread().name}/${Thread.currentThread().id}")
                    fakeDatabase.getUserDataFromPlayer(player)
                }
            }

            val result = cache[player]!!
            println("[UserDataCache/getUserDataFromPlayerAsync] Is ending on Thread:${Thread.currentThread().name}/${Thread.currentThread().id}")
            result
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
