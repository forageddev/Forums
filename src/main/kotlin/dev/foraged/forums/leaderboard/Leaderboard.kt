package dev.foraged.forums.leaderboard

import com.minexd.core.profile.Profile

abstract class Leaderboard<U>(val statisticName: String) {

    open var comparedCachedValues: Map<Profile, Pair<Int, U>> = mutableMapOf()

    abstract fun updateAndRefreshCache()
}