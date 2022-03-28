package dev.foraged.forums.forum.command

import dev.foraged.forums.Application
import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.repository.ForumRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor

@Component
class ForumCreateCommand @Autowired constructor(val forumRepository: ForumRepository) {

    init {
        Application.COMMAND_HANDLER.register(this)
    }

    @Command("forum create")
    fun execute(actor: CommandLineActor, name: String) {
        val forum = Forum(
            name = name
        )
        forumRepository.save(forum)
        actor.reply("Created forum with name ${name}")
    }
}