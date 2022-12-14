package dev.foraged.forums.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object TimeUtils
{
    private val mmssBuilder = ThreadLocal.withInitial { StringBuilder() }
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm")

    /**
     * Delegate to TimeUtils#formatIntoMMSS for backwards compat
     */
    fun formatIntoHHMMSS(secs: Int): String
    {
        return formatIntoMMSS(secs)
    }

    fun formatLongIntoHHMMSS(secs: Long): String
    {
        val unconvertedSeconds = secs.toInt()
        return formatIntoMMSS(unconvertedSeconds)
    }

    /**
     * Formats the time into a format of HH:MM:SS. Example: 3600 (1 hour) displays as '01:00:00'
     *
     * @param secs The input time, in seconds.
     * @return The HH:MM:SS formatted time.
     */
    fun formatIntoMMSS(secs: Int): String
    {
        var secs = secs
        val seconds = secs % 60
        var minutesCount = (seconds.let { secs -= it; secs } / 60).toLong()
        val minutes = minutesCount % 60L
        val hours = minutes.let { minutesCount -= it; minutesCount } / 60L
        val result = mmssBuilder.get()
        result.setLength(0)
        if (hours > 0L)
        {
            if (hours < 10L)
            {
                result.append("0")
            }
            result.append(hours)
            result.append(":")
        }
        if (minutes < 10L)
        {
            result.append("0")
        }
        result.append(minutes)
        result.append(":")
        if (seconds < 10)
        {
            result.append("0")
        }
        result.append(seconds)
        return result.toString()
    }

    fun formatLongIntoMMSS(secs: Long): String
    {
        val unconvertedSeconds = secs.toInt()
        return formatIntoMMSS(unconvertedSeconds)
    }

    /**
     * Formats time into a detailed format. Example: 600 seconds (10 minutes) displays as '10 minutes'
     *
     * @param secs The input time, in seconds.
     * @return The formatted time.
     */
    fun formatIntoDetailedString(secs: Int): String
    {
        if (secs == 0)
        {
            return "0 seconds"
        }
        val remainder = secs % 86400
        val days = secs / 86400
        val hours = remainder / 3600
        val minutes = remainder / 60 - hours * 60
        val seconds = remainder % 3600 - minutes * 60
        val fDays = if (days > 0) " " + days + " day" + (if (days > 1) "s" else "") else ""
        val fHours = if (hours > 0) " " + hours + " hour" + (if (hours > 1) "s" else "") else ""
        val fMinutes = if (minutes > 0) " " + minutes + " minute" + (if (minutes > 1) "s" else "") else ""
        val fSeconds = if (seconds > 0) " " + seconds + " second" + (if (seconds > 1) "s" else "") else ""
        return (fDays + fHours + fMinutes + fSeconds).trim { it <= ' ' }
    }

    fun formatIntoSimpleDetailedString(time: Long): String
    {
        val secs = (time / 1000L).toInt()
        if (secs == 0)
        {
            return "0 seconds"
        }
        val remainder = secs % 86400
        val days = secs / 86400
        val hours = remainder / 3600
        val minutes = remainder / 60 - hours * 60
        val seconds = remainder % 3600 - minutes * 60
        return if (days > 0)
        {
            days.toString() + " day" + if (days > 1) "s" else ""
        } else if (hours > 0)
        {
            hours.toString() + " hour" + if (hours > 1) "s" else ""
        } else if (minutes > 0)
        {
            minutes.toString() + " minute" + if (minutes > 1) "s" else ""
        } else
        {
            seconds.toString() + " second" + if (seconds > 1) "s" else ""
        }
    }

    fun formatIntoSimplifiedString(secs: Int): String
    {
        if (secs == 0)
        {
            return "0s"
        }
        val remainder = secs % 86400
        val days = secs / 86400
        val hours = remainder / 3600
        val minutes = remainder / 60 - hours * 60
        val seconds = remainder % 3600 - minutes * 60
        val fDays = if (days > 0) " " + days + "d" else ""
        val fHours = if (hours > 0) " " + hours + "h" else ""
        val fMinutes = if (minutes > 0) " " + minutes + "m" else ""
        val fSeconds = if (seconds > 0) " " + seconds + "s" else ""
        return (fDays + fHours + fMinutes + fSeconds).trim { it <= ' ' }
    }

    fun formatLongIntoDetailedString(secs: Long): String
    {
        val unconvertedSeconds = secs.toInt()
        return formatIntoDetailedString(unconvertedSeconds)
    }

    /**
     * Formats time into a format of MM/dd/yyyy HH:mm.
     *
     * @param date The Date instance to format.
     * @return The formatted time.
     */
    fun formatIntoCalendarString(date: Date?): String
    {
        return dateFormat.format(date)
    }

    /**
     * Parses a string, such as '1h4m25s' into a number of seconds.
     *
     * @param time The string to attempt to parse.
     * @return The number of seconds 'in' the given string.
     */
    fun parseTime(time: String): Int
    {
        if (time == "0" || time == "")
        {
            return 0
        }
        val lifeMatch = arrayOf("w", "d", "h", "m", "s")
        val lifeInterval = intArrayOf(604800, 86400, 3600, 60, 1)
        var seconds = 0
        for (i in lifeMatch.indices)
        {
            val matcher = Pattern.compile("([0-9]*)" + lifeMatch[i]).matcher(time)
            while (matcher.find())
            {
                seconds += matcher.group(1).toInt() * lifeInterval[i]
            }
        }
        return seconds
    }

    fun parseTimeToLong(time: String): Long
    {
        val unconvertedSeconds = parseTime(time)
        return unconvertedSeconds.toLong()
    }

    fun getSecondsBetween(a: Date, b: Date): Int
    {
        return getSecondsBetweenLong(a, b).toInt()
    }

    /**
     * Gets the seconds between date A and date B. This will never return a negative number.
     *
     * @param a Date A
     * @param b Date B
     * @return The number of seconds between date A and date B.
     */
    fun getSecondsBetweenLong(a: Date, b: Date): Long
    {
        val diff = a.time - b.time
        val absDiff = Math.abs(diff)
        return absDiff / 1000L
    }
}