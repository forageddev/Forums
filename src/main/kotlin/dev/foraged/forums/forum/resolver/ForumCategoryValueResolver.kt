/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.forum.resolver

import dev.foraged.forums.Application
import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.forum.repository.ForumCategoryRepository
import dev.foraged.forums.forum.repository.ForumRepository
import dev.foraged.forums.forum.service.impl.ForumService
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import revxrsal.commands.exception.CommandErrorException
import revxrsal.commands.process.ValueResolver

@Component
class ForumCategoryValueResolver @Autowired constructor(val repository: ForumCategoryRepository) : ValueResolver<ForumCategory>
{
    init {
        Application.COMMAND_HANDLER.registerValueResolver(ForumCategory::class.java, this)
    }

    override fun resolve(context: ValueResolver.ValueResolverContext): ForumCategory {
        return repository.findById(context.pop()).orElse(null)  ?: throw CommandErrorException("Cant find category with that name")
    }
}