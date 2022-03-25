package dev.foraged.forums.user

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : MongoRepository<User?, UUID?>
{
    fun findByUsername(username: String?): User?
    fun findByUsernameIgnoreCase(username: String?): User?
    fun findByEmail(email: String?): User?
    fun findByRegisterSecret(secret: String) : User?
    fun findByUsernameIgnoreCaseStartingWith(username: String?, pageable: Pageable?): List<User?>
}