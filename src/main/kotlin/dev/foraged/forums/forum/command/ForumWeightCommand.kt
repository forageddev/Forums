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
class ForumWeightCommand @Autowired constructor(val forumRepository: ForumRepository) {

    init {
        Application.COMMAND_HANDLER.register(this)
    }

    @Command("forum weight")
    fun execute(actor: CommandLineActor, forum: Forum, weight: Int) {
        forum.weight = weight
        forumRepository.save(forum)
        actor.reply("Updated weight of forum with name ${forum.name} to ${weight}")
    }
}