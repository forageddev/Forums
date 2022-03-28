package dev.foraged.forums.forum.command

import dev.foraged.forums.Application
import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.forum.repository.ForumCategoryRepository
import dev.foraged.forums.forum.repository.ForumRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor

@Component
class CategoryWeightCommand @Autowired constructor(val categoryRepository: ForumCategoryRepository) {

    init {
        Application.COMMAND_HANDLER.register(this)
    }

    @Command("category weight")
    fun execute(actor: CommandLineActor, category: ForumCategory, weight: Int) {
        category.weight = weight
        categoryRepository.save(category)
        actor.reply("Updated weight of forum category with name ${category.displayName} to $weight")
    }
}