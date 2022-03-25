package dev.foraged.forums.web.controller.api

import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.forum.repository.ForumCategoryRepository
import dev.foraged.forums.forum.repository.ForumRepository
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
class ForumRestController @Autowired constructor(
    val userRepository: UserRepository,
    val forumService: ForumService,
    val categoryRepository: ForumCategoryRepository,
    val categoryService: CategoryService,
    val forumRepository: ForumRepository
)
{

    @RequestMapping(value = ["/api/v1/forums/{id}/create-forum"], method = [RequestMethod.GET])
    fun createForum(@PathVariable id: String): String
    {
        val forum = Forum(name = id)

        val categories: MutableList<ForumCategory> = ArrayList()

        forum.categories = categories
        forumService.addForum(forum)
        return "Created forum"
    }

    @RequestMapping(value = ["/api/v1/forums/{id}/create-category/{name}"], method = [RequestMethod.GET])
    fun loadProfile(@PathVariable id: String?, @PathVariable name: String?): String
    {
        val forum = forumService.getForum(id)
        val category = ForumCategory(
            id = name!!.lowercase(),
            displayName = name,
            description = "test",
            forum = forum
        )

        forum.categories.add(category)
        categoryRepository.save(category)
        forumRepository.save(forum)
        return "Created new forum category"
    }
}