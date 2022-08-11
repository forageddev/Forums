package dev.foraged.forums.web.controller

import com.google.gson.internal.LinkedTreeMap
import com.minexd.core.profile.Profile
import com.minexd.core.profile.ProfileService
import com.minexd.core.rank.Rank
import com.minexd.core.rank.RankService
import com.mongodb.client.model.Filters
import dev.foraged.forums.Application
import gg.scala.store.storage.impl.MongoDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import org.bson.Document
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor

@Controller
class StaffController {

    val staffMapCache: MutableMap<Rank, Collection<Profile>> = LinkedTreeMap()
    val mediaMapCache: MutableMap<Rank, Collection<Profile>> = LinkedTreeMap()

    init {
        Application.COMMAND_HANDLER.register(this)
    }

    fun recache() {
        staffMapCache.clear()
        mediaMapCache.clear()
        println("Starting recache.")

        for (it in RankService.controller.loadAll(DataStoreStorageType.MONGO).join().values)
        {
            println(it.displayName)
            if (it.staff) staffMapCache[it] =
                ProfileService.controller.useLayerWithReturn<MongoDataStoreStorageLayer<Profile>, Collection<Profile>>(
                    DataStoreStorageType.MONGO
                ) {
                    this.loadAllWithFilterSync(Filters.elemMatch("grants", Document.parse("{ rankId: '${it.identifier}' }"))).values.filter {
                        it.bestDisplayRank.identifier == it.identifier
                    }
                }

            if ("forums.display.media" in it.getCompoundedPermissions()) mediaMapCache[it] =
                ProfileService.controller.useLayerWithReturn<MongoDataStoreStorageLayer<Profile>, Collection<Profile>>(
                    DataStoreStorageType.MONGO
                ) {
                    this.loadAllWithFilterSync(Filters.elemMatch("grants", Document.parse("{ rankId: '${it.identifier}' }"))).values.filter {
                        it.bestDisplayRank.identifier == it.identifier
                    }
                }
        }
    }

    @Command("staff recache")
    fun execute(actor: CommandLineActor) {
        val start = System.currentTimeMillis()
        recache()
        actor.reply("Recached all staff and media positions in ${System.currentTimeMillis() - start}ms.")
    }


    @RequestMapping(value = ["/staff"], method = [RequestMethod.GET])
    fun home(): ModelAndView  {
        val modelAndView = ModelAndView()

        modelAndView.addObject("data", staffMapCache)
        modelAndView.addObject("controller", ProfileService)
        modelAndView.viewName = "staff"
        return modelAndView
    }

    @RequestMapping(value = ["/media"], method = [RequestMethod.GET])
    fun famous(): ModelAndView {
        val modelAndView = ModelAndView()

        modelAndView.addObject("data", mediaMapCache)
        modelAndView.addObject("controller", ProfileService)
        modelAndView.viewName = "staff"
        return modelAndView
    }
}