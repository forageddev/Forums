package dev.foraged.forums.forum.service.impl

import dev.foraged.forums.forum.ForumCategory
import dev.foraged.forums.forum.ForumThread
import dev.foraged.forums.forum.service.ICategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
open class CategoryService : ICategoryService
{
    @Autowired lateinit var mongoTemplate: MongoTemplate

    override fun findAll(): List<ForumCategory>?
    {
        return mongoTemplate.findAll(ForumCategory::class.java)
    }

    override fun getCategory(name: String?): ForumCategory?
    {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(name))
        return mongoTemplate.findOne(query, ForumCategory::class.java)
    }

    override fun updateCategory(categoryName: String?, newCategory: ForumCategory): Boolean
    {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(categoryName))
        val category = mongoTemplate.findOne(query, ForumCategory::class.java)
        if (category != null && (newCategory.displayName.equals(category.displayName, ignoreCase = true) || getCategory(
                newCategory.displayName
            ) != null)
        )
        {
            val update = Update()
            update["name"] = newCategory.displayName
            update["description"] = newCategory.description
            update["weight"] = newCategory.weight
            update["permission"] = newCategory.permission
            mongoTemplate.updateFirst(query, update, ForumCategory::class.java)
            return true
        }
        return false
    }

    override fun updateDisplayName(categoryName: String?, displayName: String?): Boolean
    {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(categoryName))
        val category = mongoTemplate.findOne(query, ForumCategory::class.java)
        if (category != null)
        {
            val update = Update()
            update["name"] = displayName
            mongoTemplate.updateFirst(query, update, ForumCategory::class.java)
            return true
        }
        return false
    }

    override fun updateDescription(categoryName: String?, description: String?): Boolean
    {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(categoryName))
        val category = mongoTemplate.findOne(query, ForumCategory::class.java)
        if (category != null)
        {
            val update = Update()
            update["description"] = description
            mongoTemplate.updateFirst(query, update, ForumCategory::class.java)
            return true
        }
        return false
    }

    override fun updateWeight(categoryName: String?, weight: Int): Boolean
    {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(categoryName))
        val category = mongoTemplate.findOne(query, ForumCategory::class.java)
        if (category != null)
        {
            val update = Update()
            update["weight"] = weight
            mongoTemplate.updateFirst(query, update, ForumCategory::class.java)
            return true
        }
        return false
    }

    override fun updatePermission(categoryName: String?, permission: String?): Boolean
    {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(categoryName))
        val category = mongoTemplate.findOne(query, ForumCategory::class.java)
        if (category != null)
        {
            val update = Update()
            update["permission"] = permission
            mongoTemplate.updateFirst(query, update, ForumCategory::class.java)
            return true
        }
        return false
    }

    override fun removeCategory(name: String?): ForumCategory
    {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(name))
        return mongoTemplate.findAndRemove(query, ForumCategory::class.java)
    }

    override fun addThread(category: ForumCategory?, thread: ForumThread?): ForumThread?
    {
        throw NullPointerException()
    } // todo compataibility issues
}