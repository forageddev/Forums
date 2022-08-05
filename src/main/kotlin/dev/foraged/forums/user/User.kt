package dev.foraged.forums.user

import com.google.gson.JsonObject
import com.minexd.core.profile.ProfileService
import dev.foraged.forums.shop.Basket
import dev.foraged.forums.util.JsonChain
import dev.foraged.forums.util.MojangUtils
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.store.storage.storable.IDataStoreObject
import org.springframework.data.annotation.Id
import java.util.*

open class User(
    @Id override var identifier: UUID,
    var username: String = MojangUtils.fetchName(identifier)!!, // THIS MIGHT BE INNEFICINET MAYBE COME BACK AND LOOK AT DECLARING THIS VARIABLE HERE
    var email: String? = null,
    var registerSecret: String? = null,
    var password: String? = null, // ENCRPYT THIS TOO FOR OVBIOUS REASONS
    var registered: Boolean = false,
    var basket: Basket = Basket(),

    val dateJoined: Date = Date(),
    var dateLastSeen: Date = dateJoined,
) : IDataStoreObject
{    constructor(username: String, email: String, password: String) : this(
        identifier = ScalaStoreUuidCache.uniqueId(username)!!,
        email = email,
        password = password
    )

    /** Returns a map of ip addresses and date used  */
    var ipAddresses: MutableMap<Date, String> = mutableMapOf()
    var metaData: MutableMap<String, Any> = mutableMapOf() // todo future use for external systems storing data

    fun getRankColor() : String {
        return ProfileService.fetchProfile(identifier).bestDisplayRank.siteColor
    }

    fun json() : JsonObject {
        val chain = JsonChain()
            .append("identifier", identifier.toString())
            .append("username", username)

        if (email != null) chain.append("email", email)
        chain.append("registered", registered).append("dateJoined", dateJoined.time).append("dateLastSeen", dateLastSeen.time)
        return chain.complete()
    }

    override fun equals(other: Any?): Boolean
    {
        return if (other is UUID) identifier == other
        else identifier.toString() == other
    }

    override fun hashCode(): Int
    {
        var result = identifier.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (registerSecret?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + registered.hashCode()
        result = 31 * result + basket.hashCode()
        result = 31 * result + dateJoined.hashCode()
        result = 31 * result + dateLastSeen.hashCode()
        result = 31 * result + ipAddresses.hashCode()
        result = 31 * result + metaData.hashCode()
        return result
    }
}