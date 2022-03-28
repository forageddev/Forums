/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.user.resolver

import dev.foraged.forums.Application
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import revxrsal.commands.process.ValueResolver

@Component
class UserValueResolver @Autowired constructor(val userService: UserService) : ValueResolver<User?>
{
    init {
        Application.COMMAND_HANDLER.registerValueResolver(User::class.java, this)
    }

    override fun resolve(context: ValueResolver.ValueResolverContext): User?
    {
        return userService.findUserByName(context.pop())
    }
}