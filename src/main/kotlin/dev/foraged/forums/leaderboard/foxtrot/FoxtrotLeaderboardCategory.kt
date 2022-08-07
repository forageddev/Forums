package dev.foraged.forums.leaderboard.foxtrot

import dev.foraged.forums.leaderboard.LeaderboardCategory
import dev.foraged.forums.leaderboard.foxtrot.impl.FoxtrotDeathsLeaderboard
import dev.foraged.forums.leaderboard.foxtrot.impl.FoxtrotKillsLeaderboard
import dev.foraged.forums.leaderboard.foxtrot.impl.FoxtrotKillstreakLeaderboard

class FoxtrotLeaderboardCategory(
    serverId: String,
    displayName: String
) : LeaderboardCategory(
    serverId,
    displayName,
    listOf(
        FoxtrotKillsLeaderboard(serverId),
        FoxtrotDeathsLeaderboard(serverId),
        FoxtrotKillstreakLeaderboard(serverId)
    )
) {

}