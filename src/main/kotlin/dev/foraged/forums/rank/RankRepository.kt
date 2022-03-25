package dev.foraged.forums.rank

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RankRepository : MongoRepository<Rank?, String?>
{
    /**
     * Fetches a role by it's name or identification
     * @param name Name of the role
     *
     * @return Role that matches the criteria name
     */

    fun findByName(name: String?): Rank?
    fun findById(id: String): Optional<Rank?>
}