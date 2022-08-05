/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.forum.resolver

import dev.foraged.forums.Application
import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.repository.ForumRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import revxrsal.commands.exception.CommandErrorException
import revxrsal.commands.process.ValueResolver

@Component
class ForumValueResolver @Autowired constructor(val repository: ForumRepository) : ValueResolver<Forum>
{
    init {
        Application.COMMAND_HANDLER.registerValueResolver(Forum::class.java, this)
    }

    override fun resolve(context: ValueResolver.ValueResolverContext): Forum {
        return repository.getByName(context.pop()) ?: throw CommandErrorException("Cant find forum with that name")
    }
}