package dev.foraged.forums.web.controller

import dev.foraged.forums.forum.repository.ForumRepository
import dev.foraged.forums.forum.repository.ThreadRepository
import dev.foraged.forums.forum.service.impl.ForumService
import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class DebugController
{
    @Autowired
    private val userService: UserService? = null

    @Autowired
    private val forumService: ForumService? = null

    @Autowired
    private val forumRepository: ForumRepository? = null

    @Autowired
    private val threadRepository: ThreadRepository? = null
    @RequestMapping(value = ["/test"], method = [RequestMethod.GET])
    fun home(): UserDetails?
    {
        val auth = SecurityContextHolder.getContext().authentication
        val builder = StringBuilder()
        if (auth != null)
        {
            builder.append("AUTH EXISTS\n")
            if (auth.details != null)
            {
                builder.append("DETAILS EXISTS -- \n")
                return auth.principal as UserDetails
            }
        }
        return null
    }
}