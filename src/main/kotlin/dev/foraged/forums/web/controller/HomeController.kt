package dev.foraged.forums.web.controller

import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.forum.ForumThread
import dev.foraged.forums.forum.repository.CategoryRepository
import dev.foraged.forums.forum.repository.ForumRepository
import dev.foraged.forums.forum.repository.ThreadRepository
import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import java.util.stream.Collectors

@Controller
class HomeController
{
    @Autowired
    private val userService: UserService? = null

    @Autowired
    private val forumRepository: ForumRepository? = null

    @Autowired
    private val categoryRepository: CategoryRepository? = null

    @Autowired
    private val threadRepository: ThreadRepository? = null
    @RequestMapping(value = ["/", "/home"], method = [RequestMethod.GET])
    fun home(): ModelAndView
    {
        val modelAndView = ModelAndView()
        categoryRepository!!.findById("announcements").ifPresent { category: ForumCategory ->
            modelAndView.addObject(
                "announcements",
                category.threads.stream()
                    .sorted((Comparator { o1: ForumThread, o2: ForumThread -> (o1.timestamp.toInt() - o2.timestamp).toInt() } as Comparator<ForumThread>).reversed())
                    .collect(Collectors.toList()))
        }
        modelAndView.viewName = "home"
        return modelAndView
    }
}