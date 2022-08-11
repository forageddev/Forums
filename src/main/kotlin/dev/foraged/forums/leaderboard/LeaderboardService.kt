package dev.foraged.forums.leaderboard

import dev.foraged.cgs.common.CgsGameEngine
import dev.foraged.cgs.common.player.handler.CgsPlayerHandler
import dev.foraged.cgs.common.player.statistic.GameSpecificStatistics
import dev.foraged.forums.leaderboard.foxtrot.FoxtrotLeaderboardCategory
import dev.foraged.forums.leaderboard.meetup.MeetupLeaderboardCategory
import gg.scala.store.controller.DataStoreObjectControllerCache
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer

object LeaderboardService
{
    val categories = mutableListOf(
        FoxtrotLeaderboardCategory("Squads", "Squads"),
        FoxtrotLeaderboardCategory("Kits", "Kits"),
        MeetupLeaderboardCategory()
    )

    fun configure() {
        Serializers.create {
            registerTypeAdapter(
                GameSpecificStatistics::class.java,
                AbstractTypeSerializer<GameSpecificStatistics>()
            )
        }

        CgsPlayerHandler.handle = DataStoreObjectControllerCache.create()

        categories.forEach {
            it.leaderboards.forEach {
                it.updateAndRefreshCache()
            }
        }
    }

    fun findCategoryById(serveId: String) : LeaderboardCategory? {
        return categories.find {
            it.id.equals(serveId, true)
        }
    }
}