package dev.foraged.forums.ticket.service

import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.ticket.TicketTemplate

interface ITicketTemplateService
{
    /** List all templates  */
    fun findAll(): List<TicketTemplate>

    /** Retrieves a template by it's name, with it outputting  */
    fun getTemplate(name: String?): TicketTemplate

    /**
     * Add a new ticket template by a display name which automatically
     * converts into a unique identifier (replaces the spaces to dashes and is lowercase)
     *
     * @param forum Template to be added
     * @return Template created
     */
    fun addForum(forum: TicketTemplate?): TicketTemplate?
}