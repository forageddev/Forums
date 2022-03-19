package dev.foraged.forums.web.controller

import dev.foraged.forums.forum.repository.ForumRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@Controller
class LoginController
{
    @Autowired
    private val userService: UserService? = null

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
        val user = User()
        modelAndView.addObject("user", user)
        modelAndView.viewName = "register"
        return modelAndView
    }

    @RequestMapping(value = ["/register"], method = [RequestMethod.POST])
    fun createNewUser(user: @Valid User?, bindingResult: BindingResult): ModelAndView
    {
        val modelAndView = ModelAndView()
        val userExists = userService!!.findUserByName(user.getUsername())
        if (userExists != null && userExists.email != null)
        {
            bindingResult
                .rejectValue(
                    "email", "error.user",
                    "There is already a user registered with the username provided"
                )
        }
        if (bindingResult.hasErrors())
        {
            modelAndView.viewName = "register"
        } else
        {
            try
            {
                userService.createUser(user)
                modelAndView.addObject("successMessage", "User has been registered successfully")
                modelAndView.addObject("user", User())
                modelAndView.viewName = "login"
            } catch (e: Exception)
            {
                bindingResult
                    .rejectValue(
                        "uuid", "error.user",
                        "You must use a valid minecraft username."
                    )
            }
        }
        return modelAndView
    }
}