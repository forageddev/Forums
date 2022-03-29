package dev.foraged.forums.forum

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "categories")
open class ForumCategory(
    @Id var id: String,
    var displayName: String = id,
    @DBRef(lazy = true) var forum: Forum,
    var description: String = "",
    var weight: Int = -1,
    var lastActivity: Long = -1,
    var permission: String = ""
)
{
    @DBRef(lazy = true) var threads: HashSet<ForumThread> = hashSetOf()

    val friendlyUrl: String get() = "/forums/${id}"
}