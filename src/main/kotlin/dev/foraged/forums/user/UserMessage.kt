package dev.foraged.forums.user

import com.minexd.core.profile.Profile
import com.minexd.core.profile.ProfileService
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "user_messages")
class UserMessage(
    var target: UUID,
    var message: String,
    var authorId: UUID,
    var timestamp: Long = System.currentTimeMillis(),
    @Id val id: UUID = UUID.randomUUID()
) {
    @get:org.springframework.data.annotation.Transient
    val author: User get() = UserRepository.findByIdentifier(authorId)!!
    val profile: Profile get() = ProfileService.fetchProfile(authorId)
}