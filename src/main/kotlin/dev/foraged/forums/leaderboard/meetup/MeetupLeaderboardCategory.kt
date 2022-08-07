package dev.foraged.forums.leaderboard.meetup

import dev.foraged.forums.leaderboard.LeaderboardCategory
import dev.foraged.forums.leaderboard.foxtrot.impl.FoxtrotDeathsLeaderboard
import dev.foraged.forums.leaderboard.foxtrot.impl.FoxtrotKillsLeaderboard
import dev.foraged.forums.leaderboard.foxtrot.impl.FoxtrotKillstreakLeaderboard
import dev.foraged.forums.leaderboard.meetup.impl.*

object MeetupLeaderboardCategory : LeaderboardCategory(
    "meetup",
    "UHC Meetup",
    listOf(
        MeetupKillsLeaderboard,
        MeetupDeathsLeaderboard,
        MeetupKDRLeaderboard,
        MeetupWinsLeaderboard,
        MeetupLossesLeaderboard
    )
) {

}