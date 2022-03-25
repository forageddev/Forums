package dev.foraged.forums.user.punishment

import com.google.gson.JsonObject
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.util.JsonChain
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class Punishment(
    var type: PunishmentType,
    val duration: Long? = null,
    var issuedReason: String = "",
    var issuedBy: UUID? = null,
    var issuedOn: String = "",
    var issuedAt: Long = System.currentTimeMillis(),

    var removed: Boolean = false,
    var removedReason: String = "",
    var removedBy: UUID? = null,
    var removedOn: String? = null,
    var removedAt: Long? = null,

    val id: String = RandomStringUtils.randomAlphanumeric(24)
) : Comparable<Punishment>
{
    @Autowired lateinit var repository: RankRepository
    @Autowired lateinit var userRepository: UserRepository

    val active: Boolean
        get() {
            if (removedAt != null) return false
            if (duration != null && duration != Long.MAX_VALUE) {
                if (System.currentTimeMillis() >= issuedAt + duration) return false
            }
            return true
        }

    override fun compareTo(other: Punishment): Int
    {
        return (other.issuedAt - issuedAt).toInt()
    }

    fun json() : JsonObject
    {
        val chain = JsonChain()
            .append("id", id)
            .append("type", type.name)
            .append("issuedBy", issuedBy)
            .append("issuedAt", issuedAt)
            .append("issuedOn", issuedOn)
            .append("issuedReason", issuedReason)
            .append("duration", duration)

        if (removed) {
            chain
                .append("removedBy", removedBy)
                .append("removedAt", removedAt)
                .append("removedReason", removedReason)
                .append("removedOn", removedOn)
                .append("removed", removed)
        }
        return chain.complete()
    }
}