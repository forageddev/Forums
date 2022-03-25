package dev.foraged.forums.web.controller

import com.google.gson.internal.LinkedTreeMap
import dev.foraged.forums.Application
import dev.foraged.forums.rank.Rank
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor

@Controller
class StaffController @Autowired constructor(val userController: UserRepository, val rankRepository: RankRepository) {

    val staffMapCache: MutableMap<Rank, List<User>> = LinkedTreeMap()
    val famousMapCache: MutableMap<Rank, List<User>> = LinkedTreeMap()

    init {
        Application.COMMAND_HANDLER.register(this)
        recache()
    }

    fun recache() {
        rankRepository.findAll().filterNotNull().sortedByDescending { it.weight }.forEach { rank ->
            if (rank.hasPermission("minecraft.staff"))
                staffMapCache[rank] = userController.findAll().filterNotNull().filter {
                    it.primaryGrant != null && it.primaryGrant!!.rank.id == rank.id
                }

            if (rank.hasPermission("minecraft.famous")) famousMapCache[rank] = userController.findAll().filterNotNull().filter {
                it.primaryGrant!!.rank.id == rank.id
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
        modelAndView.addObject("controller", userController)
        modelAndView.viewName = "staff"
        return modelAndView
    }

    @RequestMapping(value = ["/famous"], method = [RequestMethod.GET])
    fun famous(): ModelAndView {
        val modelAndView = ModelAndView()

        modelAndView.addObject("data", famousMapCache)
        modelAndView.addObject("controller", userController)
        modelAndView.viewName = "staff"
        return modelAndView
    }
}