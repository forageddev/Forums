package dev.foraged.forums.web.controller

import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.util.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

@Controller
class StaffController
{
    @Autowired
    private val userController: UserRepository? = null

    @Autowired
    private val rankRepository: RankRepository? = null
    @RequestMapping(value = ["/staff"], method = [RequestMethod.GET])
    fun home(): ModelAndView
    {
        val modelAndView = ModelAndView()
        modelAndView.addObject("data", Utils.Companion.staffMapCache)
        modelAndView.addObject("controller", userController)
        modelAndView.viewName = "staff"
        return modelAndView
    }

    @RequestMapping(value = ["/famous"], method = [RequestMethod.GET])
    fun famous(): ModelAndView
    {
        val modelAndView = ModelAndView()
        modelAndView.addObject("data", Utils.Companion.famousMapCache)
        modelAndView.addObject("controller", userController)
        modelAndView.viewName = "staff"
        return modelAndView
    }
}