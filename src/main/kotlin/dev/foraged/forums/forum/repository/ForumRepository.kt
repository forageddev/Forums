package dev.foraged.forums.forum.repository

import dev.foraged.forums.forum.Forum
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ForumRepository : MongoRepository<Forum?, String?>
{
    /** Retrieves a section by it's name, with it outputting  */
    fun getByName(name: String?): Forum?
}