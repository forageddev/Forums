package dev.foraged.forums.user.controller

import com.minexd.core.profile.Profile
import com.minexd.core.profile.ProfileService
import dev.foraged.forums.forum.repository.ThreadRepository
import dev.foraged.forums.shop.repository.TransactionRepository
import dev.foraged.forums.ticket.repository.TicketRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserMessage
import dev.foraged.forums.user.service.UserMessageRepository
import dev.foraged.forums.user.service.UserService
import gg.scala.cache.uuid.ScalaStoreUuidCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@Controller
class UserController {
    @Autowired lateinit var userService: UserService
    @Autowired lateinit var threadRepository: ThreadRepository
    @Autowired lateinit var transactionRepository: TransactionRepository
    @Autowired lateinit var ticketRepository: TicketRepository
    @Autowired lateinit var userMessageRepository: UserMessageRepository


    @RequestMapping(value = ["/player/{id}/reply"], method = [RequestMethod.POST])
    fun addPlayerMessage(@PathVariable id: String, @RequestBody body: String, request: HttpServletRequest): String
    {
        val user = ProfileService.fetchProfile(id, false) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found")
        var message: String = ""
        body.split("&").forEach {
            if (it.startsWith("text")) {
                message = it.split("=")[1].replace("+", " ")
            }
        }
        if (message.isEmpty()) return "redirect:/player/${user.username}"

        val postUser = request.session.getAttribute("user") as User? ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "You must be logged in to post messages on player pages.")

        userMessageRepository.save(UserMessage(user.identifier, message, postUser.identifier))
        return "redirect:/player/${user.username}"
    }
    @RequestMapping(value = ["/player"], method = [RequestMethod.GET])
    fun getPlayer(request: HttpServletRequest) : String
    {
        return "redirect:/player/${request.getParameter("name")}"
    }

    @RequestMapping(value = ["/player/{id}"], method = [RequestMethod.GET])
    fun getPlayer(@PathVariable id: String): ModelAndView
    {
        val modelAndView = ModelAndView("player")
        val user = ProfileService.fetchProfile(id, false)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Player not found"
            )
        modelAndView.addObject("page", "general")
        modelAndView.addObject("transactions", transactionRepository.findAllByUser(user.identifier))
        modelAndView.addObject("user", user)
        modelAndView.addObject("messages", userMessageRepository.findAllByTarget(user.identifier).sortedByDescending { it.timestamp })
        return modelAndView
    }

    @RequestMapping(value = ["/player/{id}/stats"], method = [RequestMethod.GET])
    fun getPlayerStats(@PathVariable id: String): ModelAndView
    {
        val modelAndView = ModelAndView("player")
        val user = ProfileService.fetchProfile(id, false)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Player not found"
            )
        modelAndView.addObject("page", "statistics")
        modelAndView.addObject("transactions", transactionRepository.findAllByUser(user.identifier))
        modelAndView.addObject("user", user)
        return modelAndView
    }

    @RequestMapping(value = ["/player/{id}/threads"], method = [RequestMethod.GET])
    fun getPlayerForums(@PathVariable id: String): ModelAndView
    {
        val modelAndView = ModelAndView("player")
        val user = ProfileService.fetchProfile(id, false)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Player not found"
            )
        modelAndView.addObject("page", "forums")
        modelAndView.addObject("user", user)
        modelAndView.addObject("transactions", transactionRepository.findAllByUser(user.identifier))
        modelAndView.addObject("threads", threadRepository.findAllByAuthorId(user.identifier).sortedByDescending { it.timestamp })
        return modelAndView
    }

    @RequestMapping(value = ["/player/{id}/grants"], method = [RequestMethod.GET])
    fun getPlayerGrants(@PathVariable id: String, request: HttpServletRequest): ModelAndView
    {
        val modelAndView = getPlayer(id)
        val profile = request.session.getAttribute("profile") as Profile? ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "You must be logged in to view this page")
        if (!profile.hasPermission("forums.grants.management")) throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this page.")

        modelAndView.addObject("page", "manage")
        modelAndView.addObject("section", "grants")
        return modelAndView
    }

    @RequestMapping(value = ["/player/{id}/punishments"], method = [RequestMethod.GET])
    fun getPlayerPunishments(@PathVariable id: String, request: HttpServletRequest): ModelAndView
    {
        val modelAndView = getPlayer(id)
        val profile = request.session.getAttribute("profile") as Profile? ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "You must be logged in to view this page")
        if (!profile.bestDisplayRank.staff) throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this page.")

        modelAndView.addObject("page", "manage")
        modelAndView.addObject("section", "punishments")
        return modelAndView
    }

    @RequestMapping(value = ["/player/{id}/transactions"], method = [RequestMethod.GET])
    fun getPlayerTransactions(@PathVariable id: String, request: HttpServletRequest): ModelAndView
    {
        val modelAndView = getPlayer(id)
        val profile = request.session.getAttribute("profile") as Profile? ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "You must be logged in to view this page")
        if (!profile.hasPermission("forums.transactions.management")) throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this page.")

        modelAndView.addObject("page", "manage")
        modelAndView.addObject("section", "transactions")
        return modelAndView
    }

    @RequestMapping(value = ["/player/{id}/tickets"], method = [RequestMethod.GET])
    fun getPlayerTickets(@PathVariable id: String, request: HttpServletRequest): ModelAndView
    {
        val modelAndView = getPlayer(id)
        val profile = request.session.getAttribute("profile") as Profile? ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "You must be logged in to view this page")
        if (!profile.hasPermission("forums.tickets.management")) throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this page.")


        modelAndView.addObject("page", "manage")
        modelAndView.addObject("section", "tickets")
        modelAndView.addObject("tickets", ticketRepository.findAllByAuthorId(ScalaStoreUuidCache.uniqueId(id) ?: UUID.fromString(id)).sortedByDescending {
            it.timestamp
        })
        return modelAndView
    }


    // todo reimplment this TO CORE
/*    @RequestMapping(value = ["/player/{id}/grants/create"], method = [RequestMethod.POST])
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
    }*/
}