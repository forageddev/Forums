package dev.foraged.forums.forum

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "categories")
class ForumCategory(
    var id: String,
    var displayName: String = id,
    @DBRef var forum: Forum,
    var description: String = "",
    var weight: Int = -1,
    var lastActivity: Long = -1,
    var permission: String = ""
)
{
    /** List all threads included inside a category  */
    @DBRef var threads: HashSet<ForumThread> = hashSetOf() // Changed to only include identifiers as Threads are stored in different repo

    // Prevents allowing spaces and stuff
    val friendlyUrl: String get() = "/forums/${id}"
}