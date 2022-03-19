package dev.foraged.forums.filter

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface FilterRepository : MongoRepository<Filter?, String?>
{
    fun findByContent(content: String?): Filter?
}