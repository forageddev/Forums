package dev.foraged.forums.web.controller

import dev.foraged.forums.forum.ForumThread
import dev.foraged.forums.forum.repository.ForumCategoryRepository
import dev.foraged.forums.forum.repository.ForumRepository
import dev.foraged.forums.forum.repository.ThreadRepository
import dev.foraged.forums.forum.service.impl.ForumService
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@Controller
class ThreadController
{
    @Autowired lateinit var userService: UserService
    @Autowired lateinit var forumRepository: ForumRepository
    @Autowired lateinit var forumService: ForumService
    @Autowired lateinit var threadRepository: ThreadRepository
    @Autowired lateinit var categoryRepository: ForumCategoryRepository

    //
    // Get thread details
    //
    @RequestMapping(value = ["/thread/{id}/{title}"], method = [RequestMethod.GET])
    fun thread(@PathVariable id: String, @PathVariable(required = false) title: String?): ModelAndView
    {
        val modelAndView = ModelAndView()
        val forumThread = threadRepository!!.findById(id)
        if (forumThread.isPresent)
        {
            val thread = forumThread.get()
            modelAndView.viewName = "forums/thread"
            modelAndView.addObject("forum", thread.forum)
            modelAndView.addObject("thread", thread)
        } else
        {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Thread not found"
            )
        }
        return modelAndView
    }

    @RequestMapping(value = ["/thread/{id}"], method = [RequestMethod.GET])
    fun thread(@PathVariable id: String): ModelAndView
    {
        return thread(id, "") // Title is optional
    }

    //
    // Edit section
    //
    @RequestMapping(value = ["/thread/edit/{id}"], method = [RequestMethod.GET])
    fun editThread(@PathVariable id: String): ModelAndView
    {
        val modelAndView = ModelAndView()
        modelAndView.viewName = "forums/edit"
        val thread = threadRepository!!.findById(id)
        if (thread.isPresent)
        {         // todo Make sure they have permission to edit this thread
            modelAndView.addObject("thread", thread)
            return modelAndView
        }
        throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Thread not found"
        )
    }

    @RequestMapping(value = ["/thread/edit/{id}"], method = [RequestMethod.POST])
    fun createThread(
        @PathVariable id: String,
        @RequestBody body: @Valid String?,
        bindingResult: BindingResult?
    ): ModelAndView
    {
        val forumThread = threadRepository!!.findById(id)
        if (forumThread.isPresent)
        {         // todo Make sure they have permission to edit this thread - e.g. if they own it
            val thread = forumThread.get()
            thread.body = body!!.split("&body=".toRegex()).toTypedArray()[1]
            thread.lastEdited = System.currentTimeMillis()
            thread.lastEditedBy =
                UUID.fromString("ae171c06-36a8-4a9c-824b-393932b6a1b3") // debug todo set to user -- also make sure they're logging in LOL
            threadRepository.save(thread)
            return ModelAndView("redirect:" + thread.friendlyUrl)
                .addObject("edit", "success") // Notify that they've edited successfully
        }
        throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Thread not found"
        )
    }

    //
    // Create section
    //
    @RequestMapping(value = ["/thread/create"], method = [RequestMethod.GET])
    fun createThread(request: HttpServletRequest): ModelAndView
    {
        return createThread("", request) // Redirects to empty forum
    }

    @RequestMapping(value = ["/thread/create/{id}"], method = [RequestMethod.GET])
    fun createThread(@PathVariable id: String, request: HttpServletRequest): ModelAndView
    {
        val modelAndView = ModelAndView()
        modelAndView.viewName = "forums/new"
        if (!id.isEmpty() && !categoryRepository!!.findById(id).isPresent)
        { // Prevent trying to autofill a category that doesn't exist
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Category not found"
            )
        }
        val user = request.session.getAttribute("user") as User?
            ?: throw ResponseStatusException(
                HttpStatus.FORBIDDEN, "User not logged in" // todo We should also re-direct to /login/
            )
        modelAndView.addObject("id", id)
        modelAndView.addObject("forums", forumService.subForums)

        // todo make sure they have access to the forum they are trying to post to
        return modelAndView
    }

    @RequestMapping(value = ["/thread/create"], method = [RequestMethod.POST])
    fun createThread(
        thread: @Valid ForumThread,
        bindingResult: BindingResult?,
        request: HttpServletRequest
    ): ModelAndView
    {
        // todo make sure they have access to the forum they are trying to post to
        val subForum = categoryRepository!!.findByDisplayName(thread.forum)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Category not found"
            )
        thread.category = subForum
        val forum = subForum.forum
        // if(forum.hasAccess(user) && subForum.hasAccess(user)) // make sure it just sends an error saying u cant go there buddy!
        val user = request.session.getAttribute("user") as User
            ?: throw ResponseStatusException(
                HttpStatus.FORBIDDEN, "User not logged in" // todo We should also re-direct to /login/
            )

        thread.author = user // debug todo set to user -- also make sure they're logging in LOL
        subForum.threads.add(thread) // only stores id now
        subForum.lastActivity = System.currentTimeMillis()
        threadRepository!!.save(thread)
        categoryRepository.save(subForum)
        forumRepository!!.save(forum)
        println("Saved and updated.")

        // Redirect them to there new thread :D
        return ModelAndView("redirect:" + thread.friendlyUrl)
    }// Gotta make sure there is no thread id called that or we'll be in some issues.
}