/*
package dev.foraged.forums.web.controller.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dev.foraged.forums.Application
import dev.foraged.forums.filter.Filter
import dev.foraged.forums.filter.FilterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import javax.servlet.http.HttpServletRequest

@RestController
class FilterRestController
{
    @Autowired
    var repository: FilterRepository? = null

    @RequestMapping(value = ["/api/v1/filter/get"], method = [RequestMethod.POST])
    operator fun get(request: HttpServletRequest?): String
    {
        val response = JsonObject()
        val array = JsonArray()
        repository!!.findAll().forEach(Consumer { s: Filter ->
            val `object` = JsonObject()
            `object`.addProperty("id", s.id)
            `object`.addProperty("content", s.content)
        })
        response.addProperty("success", true)
        response.add("data", array)
        return Application.Companion.GSON.toJson(response)
    }

    @RequestMapping(value = ["/api/v1/filter/add"], method = [RequestMethod.POST])
    fun add(request: HttpServletRequest): String
    {
        val data = request.getParameter("data")
        val response = JsonObject()
        val filter = Filter()
        filter.id = (ThreadLocalRandom.current().nextInt(8999) + 1000).toString() + ""
        filter.content = data
        repository!!.save(filter)
        response.addProperty("success", true)
        return Application.Companion.GSON.toJson(response)
    }
} todo reimplimend this but better and actually functional?*/
