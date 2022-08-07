package dev.foraged.forums.leaderboard.foxtrot.impl

import com.minexd.core.profile.Profile
import com.minexd.core.profile.ProfileService
import dev.foraged.commons.CommonsShared
import dev.foraged.forums.leaderboard.Leaderboard
import java.util.*
import java.util.stream.Collectors

class FoxtrotKillstreakLeaderboard(val serverId: String) : Leaderboard<Int>("Killstreaks")
{
    override fun updateAndRefreshCache() {
        val sortedMap = mutableMapOf<Profile, Pair<Int, Int>>()

        CommonsShared.getPersistRedis().useResource {
            async().hgetall("${serverId}HighestKillstreak").thenApply { result ->
                result.entries.sortedByDescending { it.value.toInt() }.stream().limit(10).collect(Collectors.toList()).mapIndexed { index, entry ->
                    sortedMap[ProfileService.fetchProfile(UUID.fromString(entry.key))] = index to entry.value.toInt()
                }

                comparedCachedValues = sortedMap
            }
        }
    }
}