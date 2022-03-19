package dev.foraged.forums.web.controller.api

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.foraged.forums.Application
import dev.foraged.forums.rank.Rank
import dev.foraged.forums.rank.RankRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.function.Consumer
import javax.servlet.http.HttpServletRequest

@RestController
class RankRestController
{
    @Autowired
    private val rankRepository: RankRepository? = null

    @get:RequestMapping(value = ["/api/v1/ranks"], method = [RequestMethod.POST])
    val data: String
        get()
        {
            val data = JsonObject()
            val rankData = JsonArray()
            rankRepository!!.findAll().forEach(Consumer { rank: Rank ->
                println(rank.id)
                rankData.add(rank.toJson())
            })
            data.add("ranks", rankData)
            return Application.Companion.GSON.toJson(data)
        }

    @RequestMapping(value = ["/api/v1/rank/create"], method = [RequestMethod.POST])
    fun createRank(request: HttpServletRequest): String
    {
        val name = request.getParameter("name")
        val response = JsonObject()
        if (rankRepository!!.findById(name).orElse(null) == null)
        {
            val rank = Rank()
            rank.id = name
            rank.name = name
            rankRepository.save(rank)
            response.addProperty("success", true)
        } else
        {
            response.addProperty("success", false)
        }
        return Application.Companion.GSON.toJson(response)
    }

    @RequestMapping(value = ["/api/v1/rank/update"], method = [RequestMethod.POST])
    fun updateRank(request: HttpServletRequest): String
    {
        val name = request.getParameter("name")
        val response = JsonObject()
        response.addProperty("success", rankRepository!!.findById(name).isPresent)
        val rank = rankRepository.findById(name)
        if (rank.isPresent)
        {
            val data = JsonParser().parse(request.getParameter("data")).asJsonObject
            if (data.has("name")) rank.get().name = data["name"].asString
            if (data.has("prefix")) rank.get().prefix = data["prefix"].asString
            if (data.has("color")) rank.get().color = data["color"].asString
            if (data.has("forumColor")) rank.get().forumColor = data["forumColor"].asString
            if (data.has("weight")) rank.get().weight = data["weight"].asInt
            if (data.has("permissions"))
            {
                rank.get().permissions.clear()
                JsonParser().parse(data["permissions"].asString).asJsonArray.forEach(Consumer { element: JsonElement ->
                    rank.get().addPermission(element.asString)
                })
            }
            if (data.has("inherits"))
            {
                rank.get().inherits.clear()
                JsonParser().parse(data["clear"].asString).asJsonArray.forEach(Consumer { element: JsonElement ->
                    rank.get().addInherit(element.asString)
                })
            }
            rankRepository.save(rank.get())
        }
        return Application.Companion.GSON.toJson(response)
    }

    @RequestMapping(value = ["/api/v1/rank/delete"], method = [RequestMethod.POST])
    fun deleteRank(request: HttpServletRequest): String
    {
        val name = request.getParameter("name")
        val response = JsonObject()
        response.addProperty("success", rankRepository!!.findById(name).isPresent)
        if (rankRepository.findById(name).isPresent)
        {
            rankRepository.deleteById(name)
        }
        return Application.Companion.GSON.toJson(response)
    }
}