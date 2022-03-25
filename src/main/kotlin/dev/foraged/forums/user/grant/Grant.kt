package dev.foraged.forums.user.grant

import com.google.gson.JsonObject
import com.google.gson.annotations.JsonAdapter
import dev.foraged.forums.rank.Rank
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.rank.adapter.RankReferenceJsonAdapter
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.user.adapter.UserReferenceJsonAdapter
import dev.foraged.forums.util.JsonChain
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.stereotype.Component
import java.util.*

class Grant(
    @DBRef @JsonAdapter(RankReferenceJsonAdapter::class) var rank: Rank,
    var duration: Long = Long.MAX_VALUE,
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
) : Comparable<Grant>
{
    val active: Boolean
        get() {
            if (removedAt != null) return false
            if (duration != Long.MAX_VALUE) if (System.currentTimeMillis() >= issuedAt + duration) return false
            return true
        }

    override fun compareTo(other: Grant): Int {
        return (other.issuedAt - issuedAt).toInt()
    }

    fun json() : JsonObject {
        val chain = JsonChain()
            .append("id", id)
            .append("rank", rank.id)
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