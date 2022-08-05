package dev.foraged.forums.ticket.repository

import dev.foraged.forums.ticket.Ticket
import dev.foraged.forums.ticket.TicketReply
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketReplyRepository : MongoRepository<TicketReply?, String?>
{
    // find those?
    fun findTicketRepliesByTicket(ticket: Ticket) : List<TicketReply>
}