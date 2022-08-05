package dev.foraged.forums.user.service

import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserMessage
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserMessageRepository : MongoRepository<UserMessage, User> {
    fun findAllByTarget(target: UUID) : List<UserMessage>
}