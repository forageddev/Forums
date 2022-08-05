package dev.foraged.forums.ticket.command

import dev.foraged.forums.Application
import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.repository.ForumRepository
import dev.foraged.forums.ticket.TicketField
import dev.foraged.forums.ticket.TicketTemplate
import dev.foraged.forums.ticket.repository.TicketTemplateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor
import revxrsal.commands.exception.CommandErrorException
import revxrsal.commands.process.ValueResolver

@Service
class TicketCommands @Autowired constructor(val ticketTemplateRepository: TicketTemplateRepository) : ValueResolver<TicketTemplate> {

    init {
        Application.COMMAND_HANDLER.registerValueResolver(TicketTemplate::class.java, this)
        Application.COMMAND_HANDLER.register(this)
    }

    override fun resolve(context: ValueResolver.ValueResolverContext): TicketTemplate
    {
        return ticketTemplateRepository.findById(context.pop()).orElse(null) ?: throw CommandErrorException("Ticket not found")
    }

    @Command("ticket create")
    fun create(actor: CommandLineActor, id: String, displayName: String) {
        val forum = TicketTemplate(
            id = id,
            displayName = displayName
        )
        ticketTemplateRepository.save(forum)
        actor.reply("Created ticket with id $id")
    }

    @Command("ticket delete")
    fun delete(actor: CommandLineActor, forum: TicketTemplate) {
        ticketTemplateRepository.delete(forum)
        actor.reply("Delete ticket with id  ${forum.id}")
    }

    @Command("ticket description")
    fun execute(actor: CommandLineActor, forum: TicketTemplate, description: String) {
        forum.description = description
        ticketTemplateRepository.save(forum)
        actor.reply("Updated weight of ticket with name ${forum.id} to $description")
    }

    @Command("ticket weight")
    fun execute(actor: CommandLineActor, forum: TicketTemplate, weight: Int) {
        forum.weight = weight
        ticketTemplateRepository.save(forum)
        actor.reply("Updated weight of ticket with name ${forum.id} to $weight")
    }

    @Command("ticket af")
    fun execute(actor: CommandLineActor, forum: TicketTemplate, id: String, inputType: String, required: Boolean, placeholderValue: String) {
        forum.fields.add(TicketField(id, id, inputType, required, placeholderValue))
        ticketTemplateRepository.save(forum)
        actor.reply("Added new field to ticket with name ${forum.id}")
    }

    @Command("ticket mfr")
    fun execute(actor: CommandLineActor, forum: TicketTemplate, id: String, required: Boolean) {
        forum.fields.replaceAll {
            if (it.id == id) it.required = required
            it
        }
        ticketTemplateRepository.save(forum)
        actor.reply("Added new field to ticket with name ${forum.id}")
    }

    @Command("ticket mfd")
    fun execute(actor: CommandLineActor, forum: TicketTemplate, id: String, display: String) {
        forum.fields.replaceAll {
            if (it.id == id) it.displayName = display
            it
        }
        ticketTemplateRepository.save(forum)
        actor.reply("Added new field to ticket with name ${forum.id}")
    }
}