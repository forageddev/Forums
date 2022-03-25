package dev.foraged.forums.web.controller

import dev.foraged.forums.forum.repository.ForumCategoryRepository
import dev.foraged.forums.forum.repository.ForumRepository
import dev.foraged.forums.forum.repository.ThreadRepository
import dev.foraged.forums.forum.service.impl.ForumService
import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView

@Controller
class ForumController @Autowired constructor(val userService: UserService, val forumService: ForumService, val categoryRepository: ForumCategoryRepository, val forumRepository: ForumRepository, val threadRepository: ThreadRepository)
{

    @RequestMapping(value = ["/forums/{id}"], method = [RequestMethod.GET])
    fun forumCategory(@PathVariable id: String): ModelAndView
    {
        val modelAndView = ModelAndView("forums/forum")
        val subForum = categoryRepository.findById(id).orElse(null)
        if (subForum != null)
        {
            modelAndView.addObject("forum", subForum)
            modelAndView.addObject("threads", subForum.threads)
            return modelAndView
        }
        throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Category not found"
        )
    }

    @RequestMapping(value = ["/forums"], method = [RequestMethod.GET])
    fun home(): ModelAndView
    {
        val modelAndView = ModelAndView("forums")
        modelAndView.addObject("forums", forumRepository.findAll())
        println("error with model and view")
        return modelAndView
    }
}