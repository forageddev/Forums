package dev.foraged.forums.forum.service.impl

import dev.foraged.forums.forum.Forum
import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.forum.service.IForumService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

@Service
class ForumService : IForumService
{
    @Autowired
    private val mongoTemplate: MongoTemplate? = null
    override fun findAll(): List<Forum>
    {
        return mongoTemplate!!.findAll(Forum::class.java)
    }

    override fun getForum(name: String?): Forum
    {
        return mongoTemplate!!.findById(name, Forum::class.java)
    }

    override val subForums: List<ForumCategory>
        get()
        {
            val categories: MutableList<ForumCategory> = ArrayList()
            for (forum in findAll())
            {
                categories.addAll(forum.categories)
            }
            return categories
        }

    override fun addForum(forum: Forum?): Forum?
    {
        mongoTemplate!!.save(forum)
        return forum
    }
}