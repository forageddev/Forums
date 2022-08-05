package dev.foraged.forums.web.controller

import dev.foraged.forums.forum.repository.ForumRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@Controller
class LoginController @Autowired constructor(val userService: UserService)
{

    @Autowired
    private val forumRepository: ForumRepository? = null
    @RequestMapping(value = ["/login"], method = [RequestMethod.GET])
    fun login(
        @RequestParam(required = false, defaultValue = "/") redirect: String?,
        request: HttpServletRequest
    ): ModelAndView
    {
        val modelAndView = ModelAndView()
        modelAndView.viewName = "login"
        request.setAttribute("redirect", redirect)
        return modelAndView
    }

    @RequestMapping(value = ["/register"], method = [RequestMethod.GET])
    fun register(): ModelAndView
    {
        val modelAndView = ModelAndView()
        modelAndView.viewName = "register-help"
        return modelAndView
    }

    @RequestMapping(value = ["/register/{token}"], method = [RequestMethod.GET])
    fun register(@PathVariable token: String, response: HttpServletResponse): ModelAndView
    {
        val modelAndView = ModelAndView()
        val user = userService.findUserByRegisterSecret(token)
        if (user == null) {
            response.sendError(501, "Invalid or expired registration code.")
            modelAndView.viewName = "error"
        } else {
            modelAndView.addObject("registerSecret", user.registerSecret)
            modelAndView.addObject("email", user.email)
            modelAndView.addObject("username", user.username)
            modelAndView.viewName = "register"
        }

        return modelAndView
    }

    data class Register(val secret: String, val username: String, val email: String, val password: String)

    @RequestMapping(value = ["/register"], method = [RequestMethod.POST])
    fun createNewUser(@Valid register: Register, bindingResult: BindingResult): ModelAndView
    {
        val modelAndView = ModelAndView()
        val userExists = userService.findUserByRegisterSecret(register.secret)
        if (userExists == null) {
            bindingResult
                .rejectValue(
                    "secret", "error.user",
                    "Invalid or expired registration code."
                )
        }
        if (bindingResult.hasErrors()) modelAndView.viewName = "register"
        else try {
            userService.createUser(User(register.username, register.email, register.password))
            modelAndView.addObject("successMessage", "User has been registered successfully")
            modelAndView.viewName = "login"
        } catch (e: Exception) {
            bindingResult
                .rejectValue(
                    "uuid", "error.user",
                    "You must use a valid minecraft username."
                )
        }
        return modelAndView
    }
}