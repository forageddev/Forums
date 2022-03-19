package dev.foraged.forums.web.controller.api

import com.google.gson.*
import dev.foraged.forums.Application
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.user.grant.Grant
import dev.foraged.forums.user.punishment.Punishment
import dev.foraged.forums.user.punishment.PunishmentType
import dev.foraged.forums.user.service.UserService
import dev.foraged.forums.util.MojangUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.lang.Boolean
import java.util.*
import java.util.function.Consumer
import javax.servlet.http.HttpServletRequest
import kotlin.Any
import kotlin.Exception
import kotlin.Long
import kotlin.String
import kotlin.toString

@RestController
class PlayerRestController
{
    @Autowired
    private val userService: UserService? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val rankRepository: RankRepository? = null
    @RequestMapping(value = ["/api/v1/player"], method = [RequestMethod.POST])
    fun getData(request: HttpServletRequest): String
    {
        val data = JsonObject()
        var user: User? = null
        val type = request.getParameter("type")
        val value = request.getParameter("value")
        when (type)
        {
            "UUID" ->
            {
                user = userRepository!!.findById(UUID.fromString(value)).orElse(null)
            }
            "USERNAME" ->
            {
                user = userRepository!!.findByUsernameIgnoreCase(value)
            }
            "EMAIL" ->
            {
                user = userRepository!!.findByEmail(value)
            }
        }
        if (user == null && !type.equals("EMAIL", ignoreCase = true))
        {
            try
            {
                user = User()
                if (value.contains("-"))
                {
                    user.id = UUID.fromString(value)
                } else
                {
                    user.id = MojangUtils.fetchUUID(value)
                }
                user.username = MojangUtils.fetchName(user.id)
                user.dateJoined = Date(System.currentTimeMillis())
                user.dateLastSeen = Date(System.currentTimeMillis())
                user.lastServer = "hub-01"
                println("created new user")
            } catch (e: Exception)
            {
                user = null
            }
        }
        data.addProperty("success", user != null)
        println("success = $data")
        val playerData = JsonObject()
        if (user != null)
        {
            if (user.grants.isEmpty())
            {
                val grant = Grant()
                grant.id = UUID.randomUUID()
                grant.target = user.id
                grant.rankId = "default"
                grant.duration = Long.MAX_VALUE
                grant.setReason("Default Grant")
                grant.issuedAt = System.currentTimeMillis()
                user.addGrant(grant)
                println("added default grant coz not exist")
            }
            val grants = JsonArray()
            val punishments = JsonArray()
            for (grant in user.grants) grants.add(grant.toJson())
            for (punishment in user.punishments) punishments.add(punishment.toJson())
            playerData.addProperty("id", user.id.toString())
            playerData.addProperty("username", user.username)
            playerData.addProperty("email", user.email)
            if (user.lastServer != null) playerData.addProperty("lastServer", user.lastServer)
            if (user.authSecret != null) playerData.addProperty("authSecret", user.authSecret)
            if (user.lastAuthenticatedAddress != null) playerData.addProperty(
                "lastAuthenticatedAddress",
                user.lastAuthenticatedAddress
            )
            playerData.addProperty("registered", user.isRegistered)
            playerData.addProperty("dateJoined", user.dateJoined.time)
            playerData.addProperty("dateLastSeen", user.dateLastSeen.time)
            playerData.addProperty("grants", grants.toString())
            playerData.addProperty("punishments", punishments.toString())
            val metadata = JsonObject()
            user.metaData.forEach { (key: String, `val`: Any) ->
                if (key.lowercase(Locale.getDefault()).contains("options"))
                {
                    metadata.addProperty(key, ("" + `val`).toLong())
                } else
                {
                    metadata.addProperty(key, "" + `val`)
                }
            }
            playerData.add("metadata", metadata)
            userService!!.save(user)
            println("return yes good")
        }
        data.add("player", playerData)
        return Application.Companion.GSON.toJson(data)
    }

    @RequestMapping(value = ["/api/v1/player/punish"], method = [RequestMethod.POST])
    fun punishProfile(request: HttpServletRequest): String
    {
        val response = JsonObject()
        val data = JsonParser().parse(request.getParameter("punishment")).asJsonObject
        val id = UUID.fromString(request.getParameter("target"))
        val user = userRepository!!.findById(id).orElse(User())!!
        var error = false
        if (user.id == null)
        {
            try
            {
                user.id = id
                user.username = MojangUtils.fetchName(id)
                user.dateJoined = Date(System.currentTimeMillis())
                user.dateLastSeen = Date(System.currentTimeMillis())
            } catch (e: Exception)
            {
                error = true
                response.addProperty("status", "unable-to-create")
            }
        }
        if (!error)
        {
            val type = PunishmentType.valueOf(data["type"].asString)
            if (Boolean.parseBoolean(request.getParameter("undo")))
            {
                val remover = UUID.fromString(data["removedBy"].asString)
                val reason = data["removalReason"].asString
                if (user.hasActivePunishment(type))
                {
                    val punishment = user.getActivePunishment(type)
                    punishment.removedBy = remover
                    punishment.removedAt = System.currentTimeMillis()
                    punishment.removalReason = reason
                    response.addProperty("status", "success")
                } else
                {
                    response.addProperty("status", "not-found")
                }
            } else
            {
                if (user.hasActivePunishment(type))
                {
                    response.addProperty("status", "already-found")
                } else
                {
                    user.addPunishment(Punishment(data.asJsonObject))
                    response.addProperty("status", "success")
                }
            }
            userService!!.save(user)
        }
        response.addProperty("success", !error)
        return Application.Companion.GSON.toJson(response)
    }

    @RequestMapping(value = ["/api/v1/player/grant"], method = [RequestMethod.POST])
    fun grantProfile(request: HttpServletRequest): String
    {
        val response = JsonObject()
        val data = JsonParser().parse(request.getParameter("grant")).asJsonObject
        val id = UUID.fromString(request.getParameter("target"))
        val user = userRepository!!.findById(id).orElse(User())!!
        var error = false
        if (user.id == null)
        {
            try
            {
                user.id = id
                user.username = MojangUtils.fetchName(id)
                user.dateJoined = Date(System.currentTimeMillis())
                user.dateLastSeen = Date(System.currentTimeMillis())
            } catch (e: Exception)
            {
                error = true
                response.addProperty("status", "unable-to-create")
            }
        }
        if (!error)
        {
            if (Boolean.parseBoolean(request.getParameter("undo")))
            {
                val uuid = UUID.fromString(data["id"].asString)
                val remover = UUID.fromString(data["removedBy"].asString)
                val reason = data["removalReason"].asString
                println("got here")
                if (user.activeGrants.stream().anyMatch { g: Grant? -> g.getId().toString() == uuid.toString() })
                {
                    val grant =
                        user.grants.stream().filter { g: Grant -> g.id.toString() == uuid.toString() }.findFirst()
                            .orElse(null)
                    println("found grant")
                    grant.isRemoved = true
                    grant.removedBy = remover
                    grant.removedAt = System.currentTimeMillis()
                    grant.removalReason = reason
                    userService!!.save(user)
                    response.addProperty("status", "success")
                } else
                {
                    response.addProperty("status", "not-found")
                }
            } else
            {
                user.addGrant(
                    Grant(
                        data.asJsonObject,
                        id,
                        rankRepository!!.findByName(data.asJsonObject["rank"].asString)!!
                    )
                )
                response.addProperty("status", "success")
            }
            userService!!.save(user)
        }
        response.addProperty("success", !error)
        return Application.Companion.GSON.toJson(response)
    }

    @RequestMapping(value = ["/api/v1/player/status"], method = [RequestMethod.POST])
    fun updateStatus(request: HttpServletRequest): String
    {
        val id = UUID.fromString(request.getParameter("id"))
        val user = userRepository!!.findById(id).orElse(null)
        if (user != null)
        {
            user.isOnline = Boolean.parseBoolean(request.getParameter("status"))
            userService!!.save(user)
        }
        val response = JsonObject()
        response.addProperty("success", user != null)
        return Application.Companion.GSON.toJson(response)
    }

    @RequestMapping(value = ["/api/v1/player/register"], method = [RequestMethod.POST])
    fun register(request: HttpServletRequest): String
    {
        val data = JsonParser().parse(request.getParameter("data")).asJsonObject
        val id = UUID.fromString(data["id"].asString)
        val user = userRepository!!.findById(id).orElse(null)
        val response = JsonObject()
        response.addProperty("success", user != null)
        if (user == null) return response.toString()
        user.registerSecret = UUID.randomUUID().toString().replace("-", "")
        userService!!.save(user)
        return response.toString()
    }

    @RequestMapping(value = ["/api/v1/player/save"], method = [RequestMethod.POST])
    fun saveProfile(request: HttpServletRequest): String
    {
        val data = JsonParser().parse(request.getParameter("data")).asJsonObject
        val id = UUID.fromString(data["id"].asString)
        val user = userRepository!!.findById(id).orElse(User())!!
        var error = false
        if (user.id == null)
        {
            try
            {
                user.id = id
                user.username = MojangUtils.fetchName(id)
                user.dateJoined = Date(System.currentTimeMillis())
                user.dateLastSeen = Date(System.currentTimeMillis())
            } catch (e: Exception)
            {
                error = true
            }
        }
        if (!error)
        {
            if (data.has("dateLastSeen")) user.dateLastSeen = Date(data["dateLastSeen"].asLong)
            if (data.has("lastServer")) user.lastServer = data["lastServer"].asString
            if (data.has("authSecret")) user.authSecret = data["authSecret"].asString
            if (data.has("lastAuthenticatedAddress")) user.lastAuthenticatedAddress =
                data["lastAuthenticatedAddress"].asString
            if (data.has("grants"))
            {
                user.grants.clear()
                for (element in JsonParser().parse(data["grants"].asString).asJsonArray)
                {
                    user.addGrant(
                        Grant(
                            element.asJsonObject,
                            id,
                            rankRepository!!.findByName(element.asJsonObject["rank"].asString)!!
                        )
                    )
                }
            }
            if (data.has("punishments"))
            {
                user.punishments.clear()
                for (element in JsonParser().parse(data["punishments"].asString).asJsonArray)
                {
                    user.addPunishment(Punishment(element.asJsonObject))
                }
            }
            if (data.has("metadata"))
            {
                val metadata = data["metadata"].asJsonObject
                metadata.entrySet().forEach(Consumer { (key, value): Map.Entry<String, JsonElement> ->
                    if (key.lowercase(Locale.getDefault()).contains("options"))
                    {
                        user.metaData[key] = value.asLong
                    } else
                    {
                        user.metaData[key] = value.asString
                    }
                })
            }
        }
        userService!!.save(user)
        val response = JsonObject()
        response.addProperty("success", !error)
        return Application.Companion.GSON.toJson(response)
    }
}