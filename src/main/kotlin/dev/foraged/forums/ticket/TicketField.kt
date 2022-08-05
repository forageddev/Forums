package dev.foraged.forums.ticket

data class TicketField(var id: String, var displayName: String, var inputType: String, var required: Boolean = true, var placeholderValue: Any)