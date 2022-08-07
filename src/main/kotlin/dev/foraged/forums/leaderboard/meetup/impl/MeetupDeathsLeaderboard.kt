package dev.foraged.forums.leaderboard.meetup.impl

import com.minexd.core.profile.Profile
import com.minexd.core.profile.ProfileService
import dev.foraged.cgs.common.player.handler.CgsPlayerHandler
import dev.foraged.forums.leaderboard.Leaderboard
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.stream.Collectors

object MeetupDeathsLeaderboard : Leaderboard<Int>("Deaths")
{
    override fun updateAndRefreshCache() {
        val sortedMap = mutableMapOf<Profile, Pair<Int, Int>>()

        CgsPlayerHandler.handle.loadAll(DataStoreStorageType.MONGO).thenAccept {
            it.values.filterNot { (it.gameSpecificStatistics["UhcMeetupCgsStatistics"]?.deaths?.value ?: -1) == -1 }.sortedByDescending {
                it.gameSpecificStatistics["UhcMeetupCgsStatistics"]?.deaths?.value ?: -1
            }.stream().limit(10).collect(Collectors.toList()).forEachIndexed { index, player ->
                sortedMap[ProfileService.fetchProfile(player.identifier)] = index to (player.gameSpecificStatistics["UhcMeetupCgsStatistics"]?.deaths?.value ?: -1)
            }
        }
    }
}