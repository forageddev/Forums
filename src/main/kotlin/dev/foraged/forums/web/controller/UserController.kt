package dev.foraged.forums.web.controller

import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.grant.Grant
import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import javax.validation.Valid

@Controller
class UserController {
    @Autowired lateinit var userService: UserService
    @Autowired lateinit var rankRepository: RankRepository

    @RequestMapping(value = ["/player/{id}"], method = [RequestMethod.GET])
    fun getPlayer(@PathVariable id: String?): ModelAndView
    {
        val modelAndView = ModelAndView("player")
        val user = userService.findUserByName(id)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Player not found"
            )
        modelAndView.addObject("page", "general")
        modelAndView.addObject("user", user)
        return modelAndView
    }

    @RequestMapping(value = ["/player/{id}/grants"], method = [RequestMethod.GET])
    fun getPlayerGrants(@PathVariable id: String?): ModelAndView
    {
        val modelAndView = getPlayer(id)
        modelAndView.addObject("page", "manage")
        modelAndView.addObject("section", "grants")
        return modelAndView
    }

    @RequestMapping(value = ["/player/{id}/punishments"], method = [RequestMethod.GET])
    fun getPlayerPunishments(@PathVariable id: String?): ModelAndView
    {
        val modelAndView = getPlayer(id)
        modelAndView.addObject("page", "manage")
        modelAndView.addObject("section", "punishments")
        return modelAndView
    }

    @RequestMapping(value = ["/player/{id}/grants/create"], method = [RequestMethod.GET])
    fun getPlayerGrantsCreate(@PathVariable id: String?): ModelAndView
    {
        val modelAndView = getPlayer(id)
        modelAndView.addObject("page", "manage")
        modelAndView.addObject("section", "grants_add")
        return modelAndView
    }

    @RequestMapping(value = ["/player/{id}/grants/create"], method = [RequestMethod.POST])
    fun getGrants(@PathVariable id: String, grant: @Valid Grant): ModelAndView
    {
        val user = userService.findUserByName(id)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Player not found"
            )

        val rank = rankRepository.findById(grant.rank.id)
        if (rank.isPresent) {
            grant.rank = rank.get()
            user.grants.add(grant)
            return ModelAndView("redirect:/player/$id/grants")
        }
        throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Rank not found"
        )
    }
}