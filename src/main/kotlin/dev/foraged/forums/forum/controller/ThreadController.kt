package dev.foraged.forums.forum.controller

import dev.foraged.forums.forum.ForumContentReference
import dev.foraged.forums.forum.ForumThread
import dev.foraged.forums.forum.ForumThreadReply
import dev.foraged.forums.forum.repository.ForumCategoryRepository
import dev.foraged.forums.forum.repository.ForumRepository
import dev.foraged.forums.forum.repository.ThreadReplyRepository
import dev.foraged.forums.forum.repository.ThreadRepository
import dev.foraged.forums.forum.service.impl.ForumService
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@Controller
class ThreadController @Autowired constructor(val userService: UserService, val forumRepository: ForumRepository, val forumService: ForumService, val threadRepository: ThreadRepository, val categoryRepository: ForumCategoryRepository, val replyRepository: ThreadReplyRepository)
{
    //
    // Get thread details
    //
    @RequestMapping(value = ["/thread/{id}/{title}"], method = [RequestMethod.GET])
    fun thread(@PathVariable id: String, @PathVariable(required = false) title: String?): ModelAndView
    {
        val modelAndView = ModelAndView()
        val forumThread = threadRepository.findById(id)
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
        val thread = threadRepository.findById(id)
        if (thread.isPresent)
        {

            // todo Make sure they have permission to edit this thread
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
        reference: @Valid ForumContentReference,
        bindingResult: BindingResult?,
        request: HttpServletRequest
    ): ModelAndView
    {
        // todo make sure they have access to the forum they are trying to post to
        val category = categoryRepository!!.findByDisplayName(reference.category)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "Category not found"
            )
        val user = request.session.getAttribute("user") as User
        val thread = ForumThread(
            title = reference.title,
            body = reference.body,
            category = category,
            forum = category.forum,
            author = user
        )
        // add perm c heck

        category.threads.add(thread) // only stores id now
        category.lastActivity = System.currentTimeMillis()
        threadRepository.save(thread)
        categoryRepository.save(category)
        forumRepository.save(category.forum)
        println("Saved and updated.")

        // Redirect them to there new thread :D
        return ModelAndView("redirect:" + thread.friendlyUrl)
    }// Gotta make sure there is no thread id called that or we'll be in some issues.

    @RequestMapping(value = ["/thread/{id}/reply"], method = [RequestMethod.POST])
    fun replyThread(
        @PathVariable id: String,
        reference: @Valid ForumContentReference,
        bindingResult: BindingResult?,
        request: HttpServletRequest
    ): String
    {
        val user = request.session.getAttribute("user") as User
        val thread = threadRepository.findById(id).orElse(null) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Thread not found")
        // add perm c heck

        val reply = ForumThreadReply(
            body = reference.body,
            thread = thread,
            author = user
        )
        thread.replies.add(reply)

        thread.category.lastActivity = System.currentTimeMillis()
        thread.lastReply = reply
        threadRepository.save(thread)
        replyRepository.save(reply)
        categoryRepository.save(thread.category)
        forumRepository.save(thread.forum)
        println("Created reply.")

        return "redirect:" + thread.friendlyUrl
    }

    @RequestMapping(value = ["/thread/{id}/vote"], method = [RequestMethod.GET])
    fun voteThread(
        @PathVariable id: String,
        request: HttpServletRequest
    ): String
    {
        val user = request.session.getAttribute("user") as User
        val thread = threadRepository.findById(id).orElse(null) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Thread not found")
        // add perm c heck

        if (thread.upvotes.any {
            it.id == user.id
            }) thread.upvotes.removeIf {
                it.id == user.id
        }
        else thread.upvotes.add(user)

        threadRepository.save(thread)
        println("Voted.")

        return "redirect:" + thread.friendlyUrl
    }
}