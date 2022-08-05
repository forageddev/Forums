package dev.foraged.forums.user.controller.rest

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.*
import com.google.gson.*
import dev.foraged.forums.Application
import dev.foraged.forums.ForumsCredentialsProvider
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.Exception
import kotlin.String

@RestController
class PlayerRestController @Autowired constructor(
    val userService: UserService
) {
    @RequestMapping(value = ["/api/v1/player/register/"], method = [RequestMethod.POST])
    fun registerSite(request: HttpServletRequest): String {
        val data = JsonObject()
        var user: User?
        val email = request.getParameter("email")
        val value = UUID.fromString(request.getParameter("id"))

        user = userService.userRepository.findByIdentifier(value) ?: User(identifier = value)
        user.email = email
        user.registerSecret = RandomStringUtils.randomAlphanumeric(16)
        userService.save(user)

        try {
            AmazonSimpleEmailServiceClientBuilder.standard().withCredentials(ForumsCredentialsProvider).withRegion(Regions.US_WEST_1).build().sendEmail(
                SendEmailRequest()
                    .withDestination(Destination().withToAddresses(user.email))
                    .withMessage(Message()
                        .withBody(Body()
                            .withText(Content().withCharset("UTF-8").withData("You can follow this link to complete your registration on the Nyte Network!\n" +
                                    "\n" +
                                    "https://nyte.cc/register/${user.registerSecret}")))
                        .withSubject(Content().withCharset("UTF-8").withData("Nyte Network - Account Registration"))
                    )
                    .withSource("admin@nyte.cc")
            )
            data.addProperty("success", true)
        } catch (ex: Exception) {
            ex.printStackTrace()
            data.addProperty("success", false)
        }

        return Application.GSON.toJson(data)
    }
/*
    @RequestMapping(value = ["/api/v1/player/register"], method = [RequestMethod.POST])
    fun register(request: HttpServletRequest): String
    {
        val data = JsonParser().parse(request.getParameter("data")).asJsonObject
        val id = UUID.fromString(data["id"].asString)
        if (ProfileService.fetchProfile(id, false) == null) return "{\"success\":false}"

        var user = userService.userRepository.findByIdentifier(id)
        val response = JsonObject()
        response.addProperty("success", user == null)
        if (user != null) return response.toString()

        user = User(identifier = id)
        user.registerSecret = UUID.randomUUID().toString().replace("-", "")
        userService.save(user)
        return response.toString()
    }*/
}