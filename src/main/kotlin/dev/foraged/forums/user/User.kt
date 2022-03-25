package dev.foraged.forums.user

import com.google.common.collect.Lists
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dev.foraged.forums.Application
import dev.foraged.forums.rank.Rank
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.shop.Basket
import dev.foraged.forums.shop.Package
import dev.foraged.forums.user.connection.Connection
import dev.foraged.forums.user.connection.ConnectionType
import dev.foraged.forums.user.grant.Grant
import dev.foraged.forums.user.punishment.Punishment
import dev.foraged.forums.user.punishment.PunishmentType
import dev.foraged.forums.util.JsonChain
import dev.foraged.forums.util.MojangUtils
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "users")
class User(
    @Id var id: UUID,
    var username: String = MojangUtils.fetchName(id)!!, // THIS MIGHT BE INNEFICINET MAYBE COME BACK AND LOOK AT DECLARING THIS VARIABLE HERE
    var email: String? = null,
    var lastServer: String? = null,
    var authSecret: String? = null,
    var registerSecret: String? = null,
    var lastAuthenticatedAddress: String? = null, // ENSURE THIS FIELD IS ENCRYPTED AS IS SENSITIVE USER DATA
    var password: String? = null, // ENCRPYT THIS TOO FOR OVBIOUS REASONS
    var registered: Boolean = false,
    var online: Boolean = false,
    var basket: Basket = Basket(),
    val connections: MutableList<Connection> = mutableListOf(),

    val dateJoined: Date = Date(),
    var dateLastSeen: Date = dateJoined,
)
{
    constructor(username: String, email: String, password: String) : this(
        id = MojangUtils.fetchUUID(username)!!,
        email = email,
        password = password
    )

    var grants: MutableList<Grant> = mutableListOf()
    var punishments: MutableList<Punishment> = mutableListOf()

    /** Returns a map of ip addresses and date used  */
    var ipAddresses: MutableMap<Date, String> = mutableMapOf()
    var metaData: MutableMap<String, Any> = mutableMapOf() // todo future use for external systems storing data

    fun addGrant(grant: Grant) {
        grants.add(grant)
    }

    fun addPunishment(punishment: Punishment) {
        punishments.add(punishment)
    }

    fun hasActivePunishment(type: PunishmentType): Boolean {
        return getActivePunishment(type) != null
    }

    fun getActivePunishment(type: PunishmentType): Punishment? {
        for (punishment in punishments) if (punishment.active && punishment.type == type) return punishment
        return null
    }

    fun hasConnection(type: ConnectionType) : Boolean {
        return getConnection(type) != null
    }

    fun getConnection(type: ConnectionType) : Connection? {
        for (connection in connections) if (connection.type == type) return connection
        return null
    }

    // Todo get their active role -- or default when not active
    // todo add their active grant and prevent inactive (temporarily ones) from being active
    val activeGrants: List<Grant>
        get() {
            val activeGrants: MutableList<Grant> = Lists.newArrayList()
            for (grant in grants.sortedByDescending {
                it.rank.weight
            }) if (grant.active) activeGrants.add(grant)
            return activeGrants
        }
    val rankColor: String get() = primaryGrant!!.rank.hexColor

    val primaryGrant: Grant?
        get()
        {
            for (grant in activeGrants)
            {
                if (grant.active)
                {
                    return grant
                }
            }
            return null
        }

    fun hasPermission(permission: String): Boolean {
        val rank: Rank = Application.CONTEXT.beanFactory.getBean(RankRepository::class.java).findById(primaryGrant!!.rank.id).orElse(null) ?: return false

        //val rank: Rank = Utils.INSTANCE.rank(primaryGrant!!.rank.id).orElse(null) ?: return false
        return rank.hasPermission(permission)
    }

    fun json() : JsonObject {
        val chain = JsonChain()
            .append("id", id.toString())
            .append("username", username)

        if (email != null) chain.append("email", email)
        if (authSecret != null) chain.append("authSecret", authSecret)
        if (lastAuthenticatedAddress != null) chain.append("lastAuthenticatedAddress", lastAuthenticatedAddress)
        chain.append("lastServer", lastServer).append("registered", registered).append("dateJoined", dateJoined.time).append("dateLastSeen", dateLastSeen.time)


        if (grants != null) {
            val array = JsonArray()
            grants.sorted().forEach() {
                array.add(it.json())
            }
            chain.append("grants", array.toString())
        }

        if (punishments != null) {
            val array = JsonArray()
            punishments.sorted().forEach() {
                array.add(it.json())
            }
            chain.append("punishments", array.toString())
        }

        /*JsonObject metadata = new JsonObject()
        getMetaData().forEach((key, val) -> {
            metadata.addProperty(key, val.toString())
        })
        metadata.addProperty("options", options)
        metadata.addProperty("staffOptions", staffOptions)*/
        //chain.add("metadata", metadata)
        return chain.complete()
    }
}