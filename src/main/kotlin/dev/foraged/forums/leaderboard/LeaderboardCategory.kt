package dev.foraged.forums.leaderboard

import java.util.*

open class LeaderboardCategory(val id: String, val displayName: String, val leaderboards: List<Leaderboard<*>>) {

    open fun fetchPlayerStats(identifier: UUID) : MutableList<Pair<String, String>> {
        return leaderboards.map {
            it.statisticName to it.fetchPlayerStats(identifier).toString()
        }.toMutableList()
    }

    fun findLeaderboardByName(name: String) : Leaderboard<*>? {
        return leaderboards.find {
            it.statisticName.equals(name, true)
        }
    }
}