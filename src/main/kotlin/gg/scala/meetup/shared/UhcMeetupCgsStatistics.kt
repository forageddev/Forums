package gg.scala.meetup.shared
import dev.foraged.cgs.common.player.statistic.GameSpecificStatistics
import dev.foraged.cgs.common.player.statistic.value.CgsGameStatistic
import java.lang.reflect.Type

class UhcMeetupCgsStatistics(
    override var gameKills: CgsGameStatistic = CgsGameStatistic(),
    override var kills: CgsGameStatistic = CgsGameStatistic(),
    override var deaths: CgsGameStatistic = CgsGameStatistic(),
    override var played: CgsGameStatistic = CgsGameStatistic(),
    override var wins: CgsGameStatistic = CgsGameStatistic(),
    override var losses: CgsGameStatistic = CgsGameStatistic()
) : GameSpecificStatistics() {
    override fun getAbstractType(): Type = UhcMeetupCgsStatistics::class.java
}