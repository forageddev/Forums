package dev.foraged.forums.web.controller

import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.forum.repository.ForumCategoryRepository
import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

@Controller
class HomeController @Autowired constructor(val userService: UserService, val categoryRepository: ForumCategoryRepository) {

    @RequestMapping(value = ["/", "/home"], method = [RequestMethod.GET])
    fun home(): ModelAndView
    {
        val modelAndView = ModelAndView()

        categoryRepository.findById("announcements").ifPresent { category: ForumCategory ->
            modelAndView.addObject(
                "announcements",
                category.threads
            )
        }
        modelAndView.viewName = "home"
        return modelAndView
    }
}