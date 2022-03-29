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
import revxrsal.commands.exception.CommandErrorException
import revxrsal.commands.process.ValueResolver

@Service
class ForumCommands @Autowired constructor(val forumRepository: ForumRepository) : ValueResolver<Forum> {

    init {
        Application.COMMAND_HANDLER.registerValueResolver(Forum::class.java, this)
        Application.COMMAND_HANDLER.register(this)
    }

    override fun resolve(context: ValueResolver.ValueResolverContext): Forum
    {
        return forumRepository.getByName(context.pop()) ?: throw CommandErrorException("Forum not found")
    }

    @Command("forum create")
    fun create(actor: CommandLineActor, name: String) {
        val forum = Forum(
            name = name
        )
        forumRepository.save(forum)
        actor.reply("Created forum with name ${name}")
    }

    @Command("forum delete")
    fun delete(actor: CommandLineActor, forum: Forum) {
        forumRepository.delete(forum)
        actor.reply("Delete forum with name ${forum.name}")
    }

    @Command("forum weight")
    fun execute(actor: CommandLineActor, forum: Forum, weight: Int) {
        forum.weight = weight
        forumRepository.save(forum)
        actor.reply("Updated weight of forum with name ${forum.name} to ${weight}")
    }
}