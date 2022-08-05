package dev.foraged.forums.profile

import com.minexd.core.presence.PlayerPresence
import com.minexd.core.presence.PlayerPresenceHandler
import com.minexd.core.profile.Profile
import gg.scala.cache.uuid.ScalaStoreUuidCache
import java.util.Date
import java.util.UUID

class SiteProfile(identifier: UUID) : Profile(identifier)
{
    override val username: String get() = ScalaStoreUuidCache.username(identifier) ?: identifier.toString()

    override fun apply()
    {

    }


    //todo come back and make this use persistmaps or another way of fetching via REDIS
    val online: Boolean get() = (PlayerPresenceHandler.getPresence(identifier)?.state ?: PlayerPresence.State.OFFLINE) != PlayerPresence.State.OFFLINE
    //todo same for this value
    val lastServer: String get() = PlayerPresenceHandler.getPresence(identifier)?.server ?: "Unable to locate server"
    val dateLastSeen: Date get() = Date(PlayerPresenceHandler.getPresence(identifier)?.heartbeat ?: firstSeen)
}