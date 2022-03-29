package dev.foraged.forums.forum.command

import dev.foraged.forums.Application
import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.forum.repository.ForumCategoryRepository
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
@DependsOn("forumCommands")
class ForumCategoryCommands @Autowired constructor(val repository: ForumCategoryRepository, val forumRepository: ForumRepository) : ValueResolver<ForumCategory> {

    init {
        Application.COMMAND_HANDLER.registerValueResolver(ForumCategory::class.java, this)
        Application.COMMAND_HANDLER.register(this)
    }

    override fun resolve(context: ValueResolver.ValueResolverContext): ForumCategory {
        return repository.findById(context.pop()).orElse(null) ?: throw CommandErrorException("Cant find category with that name")
    }

    @Command("category create")
    fun create(actor: CommandLineActor, forum: Forum, id: String, name: String) {
        val category = ForumCategory(
            id = id,
            displayName = name,
            forum = forum
        )
        forum.categories.add(category)
        repository.save(category)
        forumRepository.save(forum)
        actor.reply("Created forum with name ${name}")
    }

    @Command("category attach")
    fun attach(actor: CommandLineActor, category: ForumCategory, forum: Forum) {
        forum.categories.add(category)
        repository.save(category)
        forumRepository.save(forum)
        actor.reply("Attached category to forum with name ${forum.name}")
    }

    @Command("category detach")
    fun detach(actor: CommandLineActor, category: ForumCategory, forum: Forum) {
        forum.categories.remove(category)
        repository.save(category)
        forumRepository.save(forum)
        actor.reply("Removed category from forum with name ${forum.name}")
    }

    @Command("category delete")
    fun delete(actor: CommandLineActor, category: ForumCategory) {
        repository.delete(category)
        actor.reply("Deleted category with name ${category.displayName}")
    }

    @Command("category description")
    fun description(actor: CommandLineActor, category: ForumCategory, description: String) {
        category.description = description
        repository.save(category)
        actor.reply("Updated description of forum category with name ${category.displayName} to $description")
    }

    @Command("category permission")
    fun permission(actor: CommandLineActor, category: ForumCategory, permission: String) {
        category.permission = permission
        repository.save(category)
        actor.reply("Updated permission of forum category with name ${category.displayName} to $permission")
    }

    @Command("category weight")
    fun execute(actor: CommandLineActor, category: ForumCategory, weight: Int) {
        category.weight = weight
        repository.save(category)
        actor.reply("Updated weight of forum category with name ${category.displayName} to $weight")
    }
}