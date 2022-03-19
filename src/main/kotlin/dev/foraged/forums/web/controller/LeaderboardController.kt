package dev.foraged.forums.web.controller

import dev.foraged.forums.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

@Controller
class LeaderboardController
{
    @Autowired
    private val userController: UserRepository? = null
    @RequestMapping(value = ["/leaderboards"], method = [RequestMethod.GET])
    fun home(): ModelAndView
    {
        val modelAndView = ModelAndView()
        modelAndView.addObject("users", userController!!.findAll())
        modelAndView.addObject("controller", userController)
        modelAndView.viewName = "leaderboards"
        return modelAndView
    }
}