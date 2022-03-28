package dev.foraged.forums.user.interceptor

import dev.foraged.forums.user.User
import org.springframework.stereotype.Service
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class UserInterceptorHandler : HandlerInterceptorAdapter() {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val user = request.session.getAttribute("user") as User?
        if (user == null) response.sendRedirect("/login")
        return user != null
    }
}