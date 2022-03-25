package dev.foraged.forums.forum.repository

import dev.foraged.forums.forum.ForumCategory
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ForumCategoryRepository : MongoRepository<ForumCategory?, String?>
{
    /** List all forum threads  */
    override fun findAll(): List<ForumCategory?>
    fun findByDisplayName(displayName: String?): ForumCategory?
}