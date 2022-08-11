package dev.foraged.forums.leaderboard

import com.minexd.core.profile.Profile
import java.util.UUID

abstract class Leaderboard<U>(val statisticName: String) {

    open var comparedCachedValues: Map<Profile, Pair<Int, U>> = mutableMapOf()

    abstract fun fetchPlayerStats(identifier: UUID) : U
    abstract fun updateAndRefreshCache()
}