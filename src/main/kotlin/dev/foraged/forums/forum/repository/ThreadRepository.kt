package dev.foraged.forums.forum.repository

import dev.foraged.forums.forum.ForumThread
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ThreadRepository : MongoRepository<ForumThread?, String?>
{
    // We gotta save all threads so we can fetch instead of querying each created forum
    /** List all forum threads  */
    override fun findAll(): List<ForumThread?>
}