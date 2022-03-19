package dev.foraged.forums.user

import com.google.common.collect.Lists
import dev.foraged.forums.rank.Rank
import dev.foraged.forums.user.grant.Grant
import dev.foraged.forums.user.punishment.Punishment
import dev.foraged.forums.user.punishment.PunishmentType
import dev.foraged.forums.util.Utils
import lombok.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Getter
@Document(collection = "users")
class User
{
    /** Returns the unique identifer for a user  */
    @Setter
    @Id
    private val id: UUID? = null

    /** Returns the unique name of a user  */
    @Indexed(unique = true, direction = IndexDirection.DESCENDING)
    @Setter
    private val username: String? = null

    /** Returns the email address of a user  */
    @Setter
    private val email: String? = null

    /** Returns the last server of a user  */
    @Setter
    private val lastServer: String? = null

    /** Returns the auth secret  */
    @Setter
    private val authSecret: String? = null

    /** Returns the register secret  */
    @Setter
    private val registerSecret: String? = null

    /** Returns the encrpyted (hash + salted) last address  */
    @Setter
    private val lastAuthenticatedAddress: String? = null

    /** Returns an encrpyted (hash + salted) password   */
    @Setter
    private val password: String? = null

    /** Returns whether a user is registered or not  */
    @Setter
    private val registered = false

    /** Returns whether a user is online or not  */
    @Setter
    private val online = false

    /** Returns all existing grants of a user including inactives one  */
    private val grants: MutableList<Grant> = ArrayList()

    /** Returns all existing punishments of a user including inactives for historical purposes  */
    private val punishments: MutableList<Punishment> = ArrayList()

    /** Returns the date upon the first registration of a user  */
    @Setter
    private val dateJoined: Date? = null

    /** Returns the date upon the last login of a user  */
    @Setter
    private val dateLastSeen // todo add a system to check if they are online or not
            : Date? = null

    /** Returns a map of ip addresses and date used  */
    private val ipAddresses: Map<Date, String> = HashMap()
    private val metaData: Map<String, Any> = HashMap() // todo future use for external systems storing data
    fun addGrant(grant: Grant)
    {
        grants.add(grant)
    }

    fun addPunishment(punishment: Punishment)
    {
        punishments.add(punishment)
    }

    fun hasActivePunishment(type: PunishmentType): Boolean
    {
        return getActivePunishment(type) != null
    }

    fun getActivePunishment(type: PunishmentType): Punishment?
    {
        for (punishment in punishments)
        {
            if (punishment.isActive && punishment.type == type) return punishment
        }
        return null
    }

    // Todo get their active role -- or default when not active
    // todo add their active grant and prevent inactive (temporarily ones) from being active
    val activeGrants: List<Grant?>
        get()
        {
            val activeGrants: MutableList<Grant?> = Lists.newArrayList()
            for (grant in Utils.Companion.sort(getGrants())) if (grant.isActive) activeGrants.add(grant)
            return activeGrants
        }
    val rankColor: String
        get() = Utils.Companion.convert(primaryGrant.getRankId())
    val primaryGrant: Grant?
        get()
        {
            for (grant in activeGrants)
            {
                if (grant!!.isActive)
                {
                    return grant
                }
            }
            return null
        }

    fun hasPermission(permission: String?): Boolean
    {
        val rank: Rank = Utils.Companion.rank(primaryGrant.getRankId())!!.orElse(null)
            ?: return false
        return rank.hasPermission(permission)
    }
}