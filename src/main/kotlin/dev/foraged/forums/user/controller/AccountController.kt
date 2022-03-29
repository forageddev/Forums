/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.user.controller

import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Controller
class AccountController @Autowired constructor(val userService: UserService) {

    @RequestMapping(value = ["/account"], method = [RequestMethod.GET])
    fun home(): ModelAndView
    {
        val modelAndView = ModelAndView()

        modelAndView.viewName = "player/account"
        return modelAndView
    }

    @RequestMapping(value = ["/account/oauth/discord"], method = [RequestMethod.GET])
    fun oauth(@RequestParam("code") code: String): ModelAndView
    {
        val modelAndView = ModelAndView()


        modelAndView.viewName = "home"
        return modelAndView
    }
}