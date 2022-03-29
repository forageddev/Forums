/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop.interceptor

import dev.foraged.forums.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ShopInterceptorHandler(val userService: UserService) : HandlerInterceptorAdapter()
{
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean
    {
        try
        {
            request.cookies.forEach {
                if (it.name.equals("guest", true)) request.session.setAttribute("guest", userService.findUserByUniqueId(UUID.fromString(it.value)))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Error whilst trying to read client cookies please clear your cache and try again.")
            // we really want to response that you are a fucking retard and should die in a hole alone but connor wont let us
        }
        return true
    }
}