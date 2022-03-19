package dev.foraged.forums.util

import java.util.*
import java.util.regex.Pattern

object UUIDs
{
    private val UUID_PATTERN =
        Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")
    private val DASHLESS_PATTERN =
        Pattern.compile("^([A-Fa-f0-9]{8})([A-Fa-f0-9]{4})([A-Fa-f0-9]{4})([A-Fa-f0-9]{4})([A-Fa-f0-9]{12})$")

    /**
     * Add dashes to a UUID.
     *
     *
     * If dashes already exist, the same UUID will be returned.
     *
     * @param uuid the UUID
     * @return a UUID with dashes
     * @throws IllegalArgumentException thrown if the given input is not actually an UUID
     */
    fun addDashes(uuid: String): String
    {
        var uuid = uuid
        uuid = uuid.replace("-", "") // Remove dashes
        val matcher = DASHLESS_PATTERN.matcher(uuid)
        require(matcher.matches()) { "Invalid UUID format" }
        return matcher.replaceAll("$1-$2-$3-$4-$5")
    }

    fun parse(source: String?): UUID?
    {
        if (source != null && !source.isEmpty())
        {
            if (UUID_PATTERN.matcher(source).find())
            {
                return UUID.fromString(source)
            } else if (DASHLESS_PATTERN.matcher(source).find())
            {
                return UUID.fromString(addDashes(source))
            }
        }
        return null
    }
}