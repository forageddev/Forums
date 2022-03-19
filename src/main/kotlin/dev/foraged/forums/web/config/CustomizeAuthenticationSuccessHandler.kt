package dev.foraged.forums.web.config

import dev.foraged.forums.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CustomizeAuthenticationSuccessHandler : AuthenticationSuccessHandler
{
    @Autowired
    private val userRepository: UserRepository? = null
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse, authentication: Authentication
    )
    {
        //set our response to OK status
        response.status = HttpServletResponse.SC_OK
        response.sendRedirect(if (request.getAttribute("redirect") == null) "/" else request.getAttribute("redirect") as String)
        request.session.setAttribute("user", userRepository!!.findByUsername(authentication.name))
        for (auth in authentication.authorities)
        {
            /*if ("ADMIN".equals(auth.getAuthority())) {
                response.sendRedirect("/dashboard");
            }*/
        }
    }
}