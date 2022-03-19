package dev.foraged.forums.web.controller

import com.google.gson.JsonObject
import dev.foraged.forums.Application
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.function.Consumer

@RestController
class SearchController
{
    @Autowired
    private val userRepository: UserRepository? = null
    @RequestMapping(
        value = ["/search-autocomplete"],
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getPlayerByName(@RequestParam query: String?): String
    {
        val queryData = JsonObject()
        userRepository!!.findByUsernameIgnoreCaseStartingWith(query, PageRequest.of(0, 5))
            .forEach(Consumer { user: User? -> queryData.addProperty(user.getUsername(), user.getId().toString()) })
        return Application.Companion.GSON.toJson(queryData)
    }
}