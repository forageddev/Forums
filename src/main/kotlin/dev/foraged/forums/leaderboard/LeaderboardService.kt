package dev.foraged.forums.leaderboard

import dev.foraged.cgs.common.player.handler.CgsPlayerHandler
import dev.foraged.forums.leaderboard.foxtrot.FoxtrotLeaderboardCategory
import dev.foraged.forums.leaderboard.meetup.MeetupLeaderboardCategory
import gg.scala.store.controller.DataStoreObjectControllerCache

object LeaderboardService
{
    val categories = mutableListOf(
        FoxtrotLeaderboardCategory("Squads", "Squads"),
        MeetupLeaderboardCategory
    )

    init {
        CgsPlayerHandler.handle = DataStoreObjectControllerCache.create()
    }

    fun findCategoryById(serveId: String) : LeaderboardCategory? {
        return categories.find {
            it.id.equals(serveId, true)
        }
    }
}