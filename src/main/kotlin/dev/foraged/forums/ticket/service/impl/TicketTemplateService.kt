package dev.foraged.forums.ticket.service.impl

import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.ticket.TicketTemplate
import dev.foraged.forums.ticket.service.ITicketTemplateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

@Service
class TicketTemplateService @Autowired constructor(val mongoTemplate: MongoTemplate) : ITicketTemplateService
{
    override fun findAll(): List<TicketTemplate>
    {
        return mongoTemplate.findAll(TicketTemplate::class.java)
    }

    override fun getTemplate(name: String?): TicketTemplate
    {
        return mongoTemplate.findById(name, TicketTemplate::class.java)
    }


    override fun addForum(forum: TicketTemplate?): TicketTemplate?
    {
        mongoTemplate.save(forum)
        return forum
    }
}