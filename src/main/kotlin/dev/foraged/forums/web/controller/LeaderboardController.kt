package dev.foraged.forums.web.controller

import com.minexd.core.profile.ProfileService
import dev.foraged.forums.user.UserRepository
import gg.scala.store.storage.type.DataStoreStorageType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView

@Controller
class LeaderboardController
{
    val userController = UserRepository

    @RequestMapping(value = ["/leaderboards"], method = [RequestMethod.GET])
    fun home(): ModelAndView
    {
        val modelAndView = ModelAndView()
        // THIS 100% NEEDS CHANGING TO A NEW SYSETM.
        modelAndView.addObject("users", ProfileService.controller.loadAll(DataStoreStorageType.MONGO).join().values)
        modelAndView.addObject("controller", userController)
        modelAndView.viewName = "leaderboards"
        return modelAndView
    }
}