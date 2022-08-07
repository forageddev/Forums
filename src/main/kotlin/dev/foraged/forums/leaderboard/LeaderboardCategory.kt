package dev.foraged.forums.leaderboard

open class LeaderboardCategory(val id: String, val displayName: String, val leaderboards: List<Leaderboard<*>>) {

    fun findLeaderboardByName(name: String) : Leaderboard<*>? {
        return leaderboards.find {
            it.statisticName.equals(name, true)
        }
    }
}