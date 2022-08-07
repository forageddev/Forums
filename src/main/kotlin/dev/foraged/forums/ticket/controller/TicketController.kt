package dev.foraged.forums.ticket.controller

import com.google.gson.JsonObject
import com.minexd.core.profile.ProfileService
import dev.foraged.forums.forum.ForumContentReference
import dev.foraged.forums.forum.ForumThread
import dev.foraged.forums.forum.ForumThreadReply
import dev.foraged.forums.forum.repository.ForumCategoryRepository
import dev.foraged.forums.forum.repository.ForumRepository
import dev.foraged.forums.forum.repository.ThreadReplyRepository
import dev.foraged.forums.forum.repository.ThreadRepository
import dev.foraged.forums.ticket.CompletedTicketField
import dev.foraged.forums.ticket.Ticket
import dev.foraged.forums.ticket.TicketReply
import dev.foraged.forums.ticket.repository.TicketReplyRepository
import dev.foraged.forums.ticket.repository.TicketRepository
import dev.foraged.forums.ticket.repository.TicketTemplateRepository
import dev.foraged.forums.ticket.service.impl.TicketTemplateService
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import gg.scala.cache.uuid.ScalaStoreUuidCache
import net.evilblock.cubed.serializers.Serializers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@Controller
class TicketController @Autowired constructor(val userService: UserService, val ticketTemplateRepository: TicketTemplateRepository, val ticketTemplateService: TicketTemplateService, val ticketRepository: TicketRepository, val ticketReplyRepository: TicketReplyRepository)
{
    @RequestMapping(value = ["/ticket"], method = [RequestMethod.GET])
    fun index() : ModelAndView {
        return ModelAndView("ticket/index")
    }

    @RequestMapping(value = ["/ticket/create"], method = [RequestMethod.GET])
    fun tickets(): ModelAndView {
        val modelAndView = ModelAndView()
        modelAndView.viewName = "ticket/templates"
        modelAndView.addObject("templates", ticketTemplateRepository.findAll())
        return modelAndView
    }
    //
    // Get thread details
    //
    @RequestMapping(value = ["/ticket/create/{id}"], method = [RequestMethod.GET])
    fun ticket(@PathVariable id: String): ModelAndView
    {
        val modelAndView = ModelAndView()
        val foundTemplate = ticketTemplateRepository.findById(id)
        if (foundTemplate.isPresent)
        {
            val template = foundTemplate.get()
            modelAndView.viewName = "ticket/create"
            modelAndView.addObject("template", template)
        } else {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Template not found"
            )
        }
        return modelAndView
    }

    @RequestMapping(value = ["/ticket/manage"], method = [RequestMethod.GET])
    fun manage(@RequestParam filter: String?, request: HttpServletRequest): ModelAndView
    {
        val modelAndView = ModelAndView()
        val tickets: MutableList<Ticket> = when (filter) {
            "ALL" -> ticketRepository.findAll()
            "RESOLVED" -> ticketRepository.findAll().filter {
                !it.open
            }
            else -> ticketRepository.findAll().filter {
                it.open
            }
        }.toMutableList()

        val user = request.session.getAttribute("user") as User? ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "You must be logged in to view this page.")
        val profile = ProfileService.getOrFetchProfile(user.identifier, false) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "You must be logged in to view this page.")
        if (!profile.bestDisplayRank.staff) tickets.removeIf {
            it.authorId != user.identifier
        }

        modelAndView.viewName = "ticket/manage"
        modelAndView.addObject("tickets", tickets)
        return modelAndView
    }

    @RequestMapping(value = ["/ticket/manage/{id}"], method = [RequestMethod.GET])
    fun thread(@PathVariable id: String, @RequestParam filter: String?, request: HttpServletRequest): ModelAndView
    {
        val modelAndView = ModelAndView()
        val foundTicket = ticketRepository.findById(id)
        if (foundTicket.isPresent)
        {
            val ticket = foundTicket.get()
            val user = request.session.getAttribute("user") as User? ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "You must be logged in to view this page.")
            val profile = ProfileService.getOrFetchProfile(user.identifier, false) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "You must be logged in to view this page.")
            if (!profile.bestDisplayRank.staff && ticket.authorId != user.identifier) throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to view this ticket.")

            modelAndView.viewName = "ticket/view"
            modelAndView.addObject("ticket", ticket)
        } else {
            val uuid = ScalaStoreUuidCache.uniqueId(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found")

            val tickets: List<Ticket> = when (filter) {
                "ALL" -> ticketRepository.findAllByAuthorId(uuid)
                "RESOLVED" -> ticketRepository.findAllByAuthorId(uuid).filter {
                    !it.open
                }
                else -> ticketRepository.findAllByAuthorId(uuid).filter {
                    it.open
                }
            }

            modelAndView.viewName = "ticket/manage"
            modelAndView.addObject("tickets", tickets)
        }
        return modelAndView
    }

    @RequestMapping(value = ["/ticket/create/{id}"], method = [RequestMethod.POST])
    fun createTicket(
        @PathVariable id: String,
        @RequestBody ref: String,
        bindingResult: BindingResult?,
        request: HttpServletRequest
    ): ModelAndView
    {
        val template = ticketTemplateRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Template not found"
            )
        val user = request.session.getAttribute("user") as User
        val ticket = Ticket(
            template = template,
            authorId = user.identifier
        )

        ref.split("&").forEach { decoded ->
            val input = decoded.split("=")[0]
            if (template.fields.any { it.id == input }) {
                ticket.fields.add(CompletedTicketField(template.fields.find { it.id == input }!!, decoded.split("=")[1].replace("+", " ")))
            }
        }

        ticketRepository.save(ticket)
        println("Saved and updated.")

        // Redirect them to there new ticket :D
        return ModelAndView("redirect:" + ticket.friendlyUrl)
    }// Gotta make sure there is no thread id called that or we'll be in some issues.

    @RequestMapping(value = ["/ticket/manage/{id}/reply"], method = [RequestMethod.POST])
    fun replyThread(
        @PathVariable id: String,
        reference: @Valid ForumContentReference,
        bindingResult: BindingResult?,
        request: HttpServletRequest
    ): String
    {
        val user = request.session.getAttribute("user") as User
        val ticket = ticketRepository.findById(id).orElse(null) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket not found")
        val profile = ProfileService.getOrFetchProfile(user.identifier, false) ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "You must be logged in to view this page.")
        if (!profile.bestDisplayRank.staff && ticket.authorId != user.identifier) throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to view this ticket.")


        val reply = TicketReply(
            body = reference.body,
            ticket = ticket,
            authorId = user.identifier
        )
        ticket.replies.add(reply)
        ticket.lastReply = reply

        ticketRepository.save(ticket)
        ticketReplyRepository.save(reply)
        println("Created reply.")

        return "redirect:" + ticket.friendlyUrl
    }
}