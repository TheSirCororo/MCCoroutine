package com.github.shynixn.mccoroutine.bungeecord.sample.impl

import com.github.shynixn.mccoroutine.bungeecord.sample.entity.UserData
import net.md_5.bungee.api.connection.ProxiedPlayer

class FakeDatabase {
    /**
     *  Simulates a getUserData call to a real database by delaying the result.
     */
    fun getUserDataFromPlayer(player: ProxiedPlayer): UserData {
        Thread.sleep(5000)
        val userData = UserData()
        userData.amountOfEntityKills = 20
        userData.amountOfPlayerKills = 30
        return userData
    }

    /**
     * Simulates a save User data call.
     */
    fun saveUserData(userData: UserData) {
        Thread.sleep(6000)
    }
}
