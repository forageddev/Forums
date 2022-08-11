package dev.foraged.forums.leaderboard.meetup.impl

import com.minexd.core.profile.Profile
import com.minexd.core.profile.ProfileService
import dev.foraged.cgs.common.player.CgsGamePlayer
import dev.foraged.cgs.common.player.handler.CgsPlayerHandler
import dev.foraged.forums.leaderboard.Leaderboard
import gg.scala.store.storage.impl.MongoDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.stream.Collectors

class MeetupWinsLeaderboard : Leaderboard<Int>("Wins")
{
    override fun fetchPlayerStats(identifier: UUID): Int {
        return CgsPlayerHandler.handle.load(identifier, DataStoreStorageType.MONGO).thenApply {
            it?.gameSpecificStatistics?.get("UhcMeetupStatistics")?.wins?.value ?: 0
        }.get() ?: 0
    }

    override fun updateAndRefreshCache() {
        val sortedMap = mutableMapOf<Profile, Pair<Int, Int>>()
        CgsPlayerHandler.handle.useLayerWithReturn<MongoDataStoreStorageLayer<CgsGamePlayer>, Map<UUID, CgsGamePlayer>>(DataStoreStorageType.MONGO) {
            this.loadAllSync()
        }.also {
            it.values.filterNot { it.gameSpecificStatistics["UhcMeetupCgsStatistics"] == null }.sortedByDescending { player ->
                player.gameSpecificStatistics["UhcMeetupCgsStatistics"]!!.wins.value
            }.stream().limit(10).collect(Collectors.toList()).forEachIndexed { index, player ->
                sortedMap[ProfileService.fetchProfile(player.identifier)] = index to player.gameSpecificStatistics["UhcMeetupCgsStatistics"]!!.wins.value
            }

            comparedCachedValues = sortedMap
        }
    }
}