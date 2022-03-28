package dev.foraged.forums.forum.command

import dev.foraged.forums.Application
import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.repository.ForumRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor

@Component
class ForumDeleteCommand @Autowired constructor(val forumRepository: ForumRepository) {

    init {
        Application.COMMAND_HANDLER.register(this)
    }

    @Command("forum delete")
    fun execute(actor: CommandLineActor, forum: Forum) {
        forumRepository.delete(forum)
        actor.reply("Delete forum with name ${forum.name}")
    }
}