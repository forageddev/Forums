package dev.foraged.forums.user.controller.rest

import com.google.gson.*
import dev.foraged.forums.Application
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.grant.Grant
import dev.foraged.forums.grant.GrantRepository
import dev.foraged.forums.punishment.Punishment
import dev.foraged.forums.punishment.PunishmentRepository
import dev.foraged.forums.punishment.PunishmentType
import dev.foraged.forums.user.service.UserService
import dev.foraged.forums.util.MojangUtils
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.lang.Boolean
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.servlet.http.HttpServletRequest
import kotlin.Exception
import kotlin.String

@RestController
class PlayerRestController @Autowired constructor(
    val punishmentRepository: PunishmentRepository,
    val grantRepository: GrantRepository,
    val userService: UserService,
    val userRepository: UserRepository,
    val rankRepository: RankRepository
) {
    @RequestMapping(value = ["/api/v1/player"], method = [RequestMethod.POST])
    fun getData(request: HttpServletRequest): String {
        val data = JsonObject()
        var user: User? = null
        val type = request.getParameter("type")
        val value = request.getParameter("value")
        when (type)
        {
            "UUID" -> user = userRepository.findById(UUID.fromString(value)).orElse(null)
            "USERNAME" -> user = userRepository.findByUsernameIgnoreCase(value)
            "EMAIL" -> user = userRepository.findByEmail(value)
        }
        if (user == null && !type.equals("EMAIL", ignoreCase = true)) {
            try {
                user = User(
                    id =
                        if (value.contains("-")) {
                            UUID.fromString(value)
                        } else {
                            MojangUtils.fetchUUID(value)!!
                        },
                    lastServer = "hub-01"
                )

                println("created new user")
                val grant = Grant(rankRepository.findById("default").get(), target = user)
                grantRepository.save(grant)
                user.grants.add(grant)
                println("and applied default rank")
            } catch (e: Exception) {
                user = null
            }
        }
        data.addProperty("success", user != null)
        println("success = $data")
        if (user != null) {
            userService.save(user)
            println("return yes good")
        }
        data.add("user", user!!.json())
        return Application.GSON.toJson(data)
    }

    @RequestMapping(value = ["/api/v1/player/register/"], method = [RequestMethod.POST])
    fun registerSite(request: HttpServletRequest): String {
        val data = JsonObject()
        var user: User? = null
        val email = request.getParameter("email")
        val value = request.getParameter("id")

        user = userRepository.findById(UUID.fromString(value)).get()
        user.registerSecret = RandomStringUtils.randomAlphanumeric(16)
        userRepository.save(user)

        val session = Session.getInstance(System.getProperties(), object: Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication
            {
                return PasswordAuthentication("no-reply@endermite.gg", "txxoptdvwxvijrwp")
            }
        })

        try {
            val message = MimeMessage(session)
            message.setFrom("no-reply@endermite.gg")
            message.addRecipient(Message.RecipientType.TO, InternetAddress(email))
            message.subject = "Nasa Network - Account Registration"

            val multipart = MimeMultipart()
            val part = MimeBodyPart()
            part.setText("You can follow this link to complete your registration on the Nasa Network!\n" +
                    "\n" +
                    "https://nasa.gg/register/${user.registerSecret}")
            multipart.addBodyPart(part)
            message.setContent(multipart)
            Transport.send(message)

            data.addProperty("success", true)
        } catch (ex: Exception) {
            ex.printStackTrace()
            data.addProperty("success", false)
        }

        return Application.GSON.toJson(data)
    }

    @RequestMapping(value = ["/api/v1/player/punish"], method = [RequestMethod.POST])
    fun punishProfile(request: HttpServletRequest): String
    {
        val response = JsonObject()
        val data = JsonParser().parse(request.getParameter("punishment")).asJsonObject
        val id = UUID.fromString(request.getParameter("target"))
        val user = userRepository.findById(id).get()

        var error = false

        val type = PunishmentType.valueOf(data["type"].asString)
        if (Boolean.parseBoolean(request.getParameter("undo"))) {
            val remover = UUID.fromString(data["removedBy"].asString)
            val reason = data["removedReason"].asString
            if (user.hasActivePunishment(type)) {
                val punishment = user.getActivePunishment(type)!!
                punishment.removedBy = remover
                punishment.removedAt = System.currentTimeMillis()
                punishment.removedReason = reason
                punishment.removed = true
                punishmentRepository.save(punishment)
                response.addProperty("status", "success")
            } else {
                response.addProperty("status", "not-found")
                error = true
            }
        } else {
            if (user.hasActivePunishment(type)) {
                response.addProperty("status", "already-found")
                error = true
            } else {
                val punishment = Application.GSON.fromJson(data.asJsonObject, Punishment::class.java)

                user.addPunishment(punishment)
                punishmentRepository.save(punishment)
                response.addProperty("status", "success")
            }
        }
        userService.save(user)

        response.addProperty("success", !error)
        return Application.GSON.toJson(response)
    }

    @RequestMapping(value = ["/api/v1/player/grant"], method = [RequestMethod.POST])
    fun grantProfile(request: HttpServletRequest): String
    {
        val response = JsonObject()
        val data = JsonParser().parse(request.getParameter("grant")).asJsonObject
        val targetUuid = UUID.fromString(request.getParameter("target"))
        val user = userRepository.findById(targetUuid).get()
        var error = false

        if (!error) {
            if (Boolean.parseBoolean(request.getParameter("undo"))) {
                val id = data["id"].asString
                val remover = UUID.fromString(data["removedBy"].asString)
                val reason = data["removedReason"].asString
                println("got here")
                if (user.activeGrants.any { it.id == id.toString() }) //) .stream().anyMatch { g: Grant? -> g.getId().toString() == uuid.toString() })
                {
                    val grant = grantRepository.findById(id).get()
                    println("found grant")
                    grant.removed = true
                    grant.removedBy = remover
                    grant.removedAt = System.currentTimeMillis()
                    grant.removedReason = reason
                    userService.save(user)
                    response.addProperty("status", "success")
                    grantRepository.save(grant)
                } else {
                    response.addProperty("status", "not-found")
                }
            } else {
                val grant = Application.GSON.fromJson(data.asJsonObject, Grant::class.java)
                grant.rank = Application.CONTEXT.beanFactory.getBean(RankRepository::class.java).findById(data.asJsonObject["rank"].asString).get()
                grant.target = user
                grantRepository.save(grant)

                response.addProperty("status", "success")
            }
            userService.save(user)
        }
        response.addProperty("success", !error)
        return Application.GSON.toJson(response)
    }

    @RequestMapping(value = ["/api/v1/player/status"], method = [RequestMethod.POST])
    fun updateStatus(request: HttpServletRequest): String
    {
        val id = UUID.fromString(request.getParameter("id"))
        val user = userRepository.findById(id).orElse(null)
        if (user != null) {
            user.online = Boolean.parseBoolean(request.getParameter("status"))
            userService.save(user)
        }
        val response = JsonObject()
        response.addProperty("success", user != null)
        return Application.GSON.toJson(response)
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
        val user = userRepository.findById(id).get()!!

        var error = false
        if (user.id == null) {
            try
            {
                user.id = id
                user.username = MojangUtils.fetchName(id)!!
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
            if (data.has("lastAuthenticatedAddress")) user.lastAuthenticatedAddress = data["lastAuthenticatedAddress"].asString
            // todo some meta data saving or osmthing honestly most this shit needs re-writing at some point#
            // its a fucking shit show lmao
        }
        userService.save(user)
        val response = JsonObject()
        response.addProperty("success", !error)
        return Application.GSON.toJson(response)
    }
}