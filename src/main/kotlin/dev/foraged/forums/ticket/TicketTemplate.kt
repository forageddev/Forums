package dev.foraged.forums.ticket

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "ticket_templates")
open class TicketTemplate(
    @Id var id: String,
    var displayName: String = id,
    var description: String = "",
    var weight: Int = -1,
    var fields: MutableList<TicketField> = mutableListOf()
)
{
    val friendlyUrl: String get() = "/ticket/create/$id"
}