package dev.foraged.forums.leaderboard.controller

import com.minexd.core.profile.ProfileService
import dev.foraged.forums.Application
import dev.foraged.forums.leaderboard.Leaderboard
import dev.foraged.forums.leaderboard.LeaderboardCategory
import dev.foraged.forums.leaderboard.LeaderboardService
import dev.foraged.forums.user.UserRepository
import gg.scala.store.storage.type.DataStoreStorageType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor
import java.util.concurrent.CompletableFuture

@Controller
class LeaderboardController
{
    val userController = UserRepository
    val leaderboardService = LeaderboardService


    init {
        Application.COMMAND_HANDLER.register(this)
    }

    @Command("leaderboard refresh")
    fun refresh(actor: CommandLineActor) {
        LeaderboardService.categories.forEach {
            it.leaderboards.forEach(Leaderboard<*>::updateAndRefreshCache)
        }
        actor.reply("refreshed all leaderboards")
    }

    @RequestMapping(value = ["/leaderboards/{id}"], method = [RequestMethod.GET])
    fun home(@PathVariable("id") id: String): ModelAndView
    {
        val modelAndView = ModelAndView()

        return CompletableFuture.supplyAsync {
            val category = leaderboardService.findCategoryById(id) ?: throw ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "There is no leaderboard defined with that identifier.")

            modelAndView.addObject("category", category)
            modelAndView.addObject("controller", userController)
            modelAndView.viewName = "leaderboards"

            return@supplyAsync modelAndView
        }.join()
    }
}