package dev.foraged.forums.web.config

import com.minexd.core.profile.ProfileService
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
    val userRepository = UserRepository

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse, authentication: Authentication
    )
    {
        //set our response to OK status
        response.status = HttpServletResponse.SC_OK
        response.sendRedirect(if (request.getAttribute("redirect") == null) "/" else request.getAttribute("redirect") as String)
        val user = userRepository.findByUsername(authentication.name) ?: throw RuntimeException("Authentication not found.")
        request.session.setAttribute("user", user)
        request.session.setAttribute("profile", ProfileService.fetchProfile(user.identifier))
        for (auth in authentication.authorities)
        {
            /*if ("ADMIN".equals(auth.getAuthority())) {
                response.sendRedirect("/dashboard");
            }*/
        }
    }
}