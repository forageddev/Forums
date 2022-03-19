package dev.foraged.forums.forum.controller

import dev.foraged.forums.Application
import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.forum.ForumThread
import dev.foraged.forums.forum.repository.CategoryRepository
import dev.foraged.forums.forum.repository.ForumRepository
import dev.foraged.forums.forum.repository.ThreadRepository
import dev.foraged.forums.forum.service.ICategoryService
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

@RestController
@RequestMapping("/api/forum/category")
class CategoryController
{
    private val LOGGER = LoggerFactory.getLogger(javaClass)
    private val categoryDAL: ICategoryService? = null

    @Autowired lateinit var forumService: ForumRepository
    @Autowired lateinit var userService: UserService
    @Autowired lateinit var threadRepository: ThreadRepository
    @Autowired lateinit var categoryRepository: CategoryRepository

    fun createThread(forum: ForumCategory, author: User, title: String, content: String?)
    {
        var id: Int = Application.Companion.RANDOM.nextInt(900) + 100
        while (!forum.threads.isEmpty() && forum.threads.contains(id.toString()))
        {
            id++
        }
        val thread = ForumThread(id.toString(), author, title)
        thread.body = content
        forum.threads.add(thread)
        threadRepository!!.save(thread) // Adds the thread to global thread collection
        createReply(thread, author, "This is a reply")
        println("Created thread $id")
    }

    fun createReply(thread: ForumThread, author: User, content: String?)
    {
        val id = AtomicInteger(Application.Companion.RANDOM.nextInt(900) + 100)
        thread.replies.forEach(Consumer { reply: ForumThread ->
            if (reply.id.toInt() == id.get())
            {
                id.getAndIncrement()
            }
        })
        val reply = ForumThread(id.get().toString(), author)
        reply.setBody(content)
        thread.replies.add(reply)
        println("Added reply " + id + " for " + thread.id)
    }

    @get:RequestMapping(method = [RequestMethod.GET])
    val categories: List<ForumCategory?>?
        get() = categoryDAL!!.findAll()

    @RequestMapping(value = ["/{category}"], method = [RequestMethod.GET])
    fun getCategory(@PathVariable category: String?): ForumCategory?
    {
        return categoryDAL!!.getCategory(category)
    }

    @RequestMapping(
        value = ["/{category}/update"],
        method = [RequestMethod.GET],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateCategory(@PathVariable category: String, @RequestBody updatedCategory: ForumCategory): Boolean
    {
        LOGGER.info("Category $category was updated")
        return categoryDAL!!.updateCategory(category, updatedCategory)
    }

    @RequestMapping(
        value = ["/{category}/update/name"],
        method = [RequestMethod.GET],
        consumes = [MediaType.TEXT_PLAIN_VALUE]
    )
    fun updateDisplayName(@PathVariable category: String, @RequestBody name: String): Boolean
    {
        LOGGER.info("Category $category display name was set to $name.")
        return categoryDAL!!.updateDisplayName(category, name)
    }

    @RequestMapping(
        value = ["/{category}/update/description"],
        method = [RequestMethod.GET],
        consumes = [MediaType.TEXT_PLAIN_VALUE]
    )
    fun updateDescription(@PathVariable category: String, @RequestBody description: String): Boolean
    {
        LOGGER.info("Category $category description was set to $description.")
        return categoryDAL!!.updateDescription(category, description)
    }

    @RequestMapping(
        value = ["/{category}/update/permission"],
        method = [RequestMethod.GET],
        consumes = [MediaType.TEXT_PLAIN_VALUE]
    )
    fun updatePermission(@PathVariable category: String, @RequestBody permission: String): Boolean
    {
        LOGGER.info("Category $category permission was set to $permission.")
        return categoryDAL!!.updatePermission(category, permission)
    }

    @RequestMapping(
        value = ["/{category}/update/weight"],
        method = [RequestMethod.GET],
        consumes = [MediaType.TEXT_PLAIN_VALUE]
    )
    fun updateWeight(@PathVariable category: String, @RequestBody weight: Int): Boolean
    {
        LOGGER.info("Category $category weight was set to $weight.")
        return categoryDAL!!.updateWeight(category, weight)
    }

    @RequestMapping(value = ["/add/{category}"], method = [RequestMethod.GET])
    fun addCategory(@PathVariable category: String?): ForumCategory?
    {
        return null
    }

    @RequestMapping(value = ["/remove/{category}"], method = [RequestMethod.GET])
    fun removeCategory(@PathVariable category: String?): ForumCategory?
    {
        return categoryDAL!!.removeCategory(category)
    }

    companion object
    {
        /** Shows how many category threads are visible per page  */
        const val THREADS_PER_PAGE = 10
    }
}