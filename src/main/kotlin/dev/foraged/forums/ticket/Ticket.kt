package dev.foraged.forums.ticket

import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "tickets")
open class Ticket(
    @Id var id: String = RandomStringUtils.randomAlphanumeric(12),
    var authorId: UUID,

    @DBRef var template: TicketTemplate, // template ticket was created from

    var fields: MutableList<CompletedTicketField> = mutableListOf(), // the ticket fields with the values of each field submitted
    val timestamp: Long = System.currentTimeMillis(),
    var lastEdited: Long? = null,
    var lastEditedBy: UUID? = null,
    var open: Boolean = true // if false people wont be able to post replies
)
{
    @DBRef var replies: LinkedList<TicketReply> = LinkedList()
    @DBRef var lastReply: TicketReply? = null

    @org.springframework.data.annotation.Transient val author: User = UserRepository.findByIdentifier(authorId)!!

    val friendlyUrl: String get() = "/ticket/manage/$id"
}