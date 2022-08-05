package dev.foraged.forums.ticket.repository

import dev.foraged.forums.forum.ForumThread
import dev.foraged.forums.ticket.Ticket
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TicketRepository : MongoRepository<Ticket, String?>
{
    // We gotta save all threads so we can fetch instead of querying each created forum
    /** List all tickets  */
    override fun findAll(): List<Ticket>
    fun findAllByAuthorId(authorId: UUID) : List<Ticket>
}