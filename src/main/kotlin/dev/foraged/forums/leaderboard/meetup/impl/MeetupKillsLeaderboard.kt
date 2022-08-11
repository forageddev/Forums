package dev.foraged.forums.leaderboard.meetup.impl

import com.minexd.core.profile.Profile
import com.minexd.core.profile.ProfileService
import dev.foraged.cgs.common.player.CgsGamePlayer
import dev.foraged.cgs.common.player.handler.CgsPlayerHandler
import dev.foraged.forums.leaderboard.Leaderboard
import gg.scala.store.storage.impl.MongoDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.serializers.Serializers
import java.util.*
import java.util.stream.Collectors

class MeetupKillsLeaderboard : Leaderboard<Int>("Kills")
{
    override fun fetchPlayerStats(identifier: UUID): Int {
        return CgsPlayerHandler.handle.load(identifier, DataStoreStorageType.MONGO).thenApply {
            it?.gameSpecificStatistics?.get("UhcMeetupStatistics")?.kills?.value ?: 0
        }.get() ?: 0
    }

    override fun updateAndRefreshCache() {
        val sortedMap = mutableMapOf<Profile, Pair<Int, Int>>()

        CgsPlayerHandler.handle.useLayerWithReturn<MongoDataStoreStorageLayer<CgsGamePlayer>, Map<UUID, CgsGamePlayer>>(DataStoreStorageType.MONGO) {
            this.loadAllSync()
        }.also {
            println("kills data")
            println(Serializers.gson.toJson(it.values))
            it.values.filterNot { it.gameSpecificStatistics["UhcMeetupCgsStatistics"] == null }.sortedByDescending { player ->
                player.gameSpecificStatistics["UhcMeetupCgsStatistics"]!!.kills.value
            }.stream().limit(10).collect(Collectors.toList()).forEachIndexed { index, player ->
                sortedMap[ProfileService.fetchProfile(player.identifier)] = index to player.gameSpecificStatistics["UhcMeetupCgsStatistics"]!!.kills.value
            }

            comparedCachedValues = sortedMap
        }
    }
}