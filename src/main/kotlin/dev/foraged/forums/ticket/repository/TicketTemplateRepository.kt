package dev.foraged.forums.ticket.repository

import dev.foraged.forums.ticket.TicketTemplate
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketTemplateRepository : MongoRepository<TicketTemplate?, String?>
{
    // We gotta save all threads so we can fetch instead of querying each created forum
    /** List all tickets  */
    override fun findAll(): List<TicketTemplate?>
}