package dev.foraged.forums.web.controller.api

import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.forum.repository.CategoryRepository
import dev.foraged.forums.forum.service.impl.CategoryService
import dev.foraged.forums.forum.service.impl.ForumService
import dev.foraged.forums.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class ForumRestController
{
    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val forumService: ForumService? = null

    @Autowired
    private val categoryService: CategoryService? = null

    @Autowired
    private val categoryRepository: CategoryRepository? = null
    @RequestMapping(value = ["/api/v1/forums/{id}/create-forum"], method = [RequestMethod.GET])
    fun createForum(@PathVariable id: String): String
    {
        val forum = Forum()
        forum.name = id
        forum.weight = 1
        val categories: MutableList<ForumCategory> = ArrayList()
        val category = ForumCategory()
        category.id = id.lowercase(Locale.getDefault())
        category.displayName = id
        category.description = "Test"
        category.forum = forum
        category.weight = 1
        categoryRepository!!.save(category)
        categories.add(category)
        forum.categories = categories
        forumService!!.addForum(forum)
        return "Created forum"
    }

    @RequestMapping(value = ["/api/v1/forums/{id}/create-category/{name}"], method = [RequestMethod.GET])
    fun loadProfile(@PathVariable id: String?, @PathVariable name: String): String
    {
        val forum = forumService!!.getForum(id)
        val category = ForumCategory()
        category.id = name.lowercase(Locale.getDefault())
        category.displayName = name
        category.description = "Test"
        category.forum = forum
        category.weight = 1
        forum.categories.add(category)
        forumService.addForum(forum)
        return "Created new forum category"
    }
}