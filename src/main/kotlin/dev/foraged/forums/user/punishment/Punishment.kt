package dev.foraged.forums.user.punishment

import com.google.gson.JsonObject
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.util.MojangUtils
import lombok.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.lang.NonNull
import java.util.*

@Getter
@Setter
@NoArgsConstructor
class Punishment(`object`: JsonObject)
{
    @Autowired
    private val repository: RankRepository? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Id
    @NonNull
    private var id = UUID.randomUUID()
    private val type: PunishmentType
    var reason: String
    private val issuedBy: UUID?
    private var issuedAt = System.currentTimeMillis()
    private val duration: Long?
    private val removalReason: String? = null
    private val removedBy: UUID? = null
    private val removedAt: Long? = null
    val isRemoved: Boolean
        get() = removedAt != null
    val issuedByName: String?
        get() = try
        {
            MojangUtils.fetchName(issuedBy)
        } catch (x: Exception)
        {
            "Console"
        }
    val issuedByStyles: String?
        get() = try
        {
            userRepository!!.findById(issuedBy).orElse(null).rankColor
        } catch (x: Exception)
        {
            "text-danger"
        }
    val removedByName: String?
        get() = try
        {
            MojangUtils.fetchName(removedBy)
        } catch (x: Exception)
        {
            "Console"
        }
    val removedByStyles: String?
        get() = try
        {
            userRepository!!.findById(removedBy).orElse(null).rankColor
        } catch (x: Exception)
        {
            "text-danger"
        }
    val isActive: Boolean
        get()
        {
            if (removedAt == null)
            {
                if (duration != null && duration != Long.MAX_VALUE)
                {
                    if (System.currentTimeMillis() >= issuedAt + duration)
                    {
                        return false
                    }
                }
                return true
            }
            return false
        }

    fun toJson(): JsonObject
    {
        val `object` = JsonObject()
        `object`.addProperty("uniqueId", getId().toString())
        `object`.addProperty("type", getType().name)
        `object`.addProperty("issuedBy", if (getIssuedBy() == null) null else getIssuedBy().toString())
        `object`.addProperty("issuedAt", getIssuedAt())
        `object`.addProperty("reason", getReason())
        `object`.addProperty("duration", getDuration())
        if (isRemoved)
        {
            `object`.addProperty("removed", isRemoved)
            `object`.addProperty("removedBy", if (getRemovedBy() == null) null else getRemovedBy().toString())
            `object`.addProperty("removedAt", getRemovedAt())
            `object`.addProperty("removedReason", getRemovalReason())
        }
        return `object`
    }

    init
    {
        id = UUID.fromString(`object`["uniqueId"].asString)
        type = PunishmentType.valueOf(`object`["type"].asString)
        issuedBy = if (`object`["issuedBy"].isJsonNull) null else UUID.fromString(`object`["issuedBy"].asString)
        issuedAt = `object`["issuedAt"].asLong
        reason = `object`["reason"].asString
        duration = `object`["duration"].asLong
        if (`object`.has("removed") && `object`["removed"].asBoolean)
        {
            if (!`object`["removedBy"].isJsonNull) setRemovedBy(UUID.fromString(`object`["removedBy"].asString))
            if (!`object`["removedAt"].isJsonNull) setRemovedAt(`object`["removedAt"].asLong)
            if (!`object`["removedReason"].isJsonNull) setRemovalReason(`object`["removedReason"].asString)
        }
    }
}