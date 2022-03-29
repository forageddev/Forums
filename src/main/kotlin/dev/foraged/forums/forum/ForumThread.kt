package dev.foraged.forums.forum

import dev.foraged.forums.Application
import dev.foraged.forums.user.User
import org.apache.commons.lang3.RandomStringUtils
import org.commonmark.node.Node
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "threads")
open class ForumThread(
    @Id var id: String = RandomStringUtils.randomAlphanumeric(12),
    @DBRef var author: User? = null,
    var title: String = "",
    var body: String = "",

    @DBRef var category: ForumCategory, // forum category is stored in
    @DBRef(lazy = true) var forum: Forum, // forum is stored in
    val timestamp: Long = System.currentTimeMillis(),
    var lastEdited: Long? = null,
    var lastEditedBy: UUID? = null,
    @DBRef var upvotes: MutableList<User> = mutableListOf(), // store this in a list so we can determine what users upvoted
    var open: Boolean = true // if false people wont be able to post replies
)
{
    @DBRef var replies: LinkedList<ForumThreadReply> = LinkedList()
    @DBRef var lastReply: ForumThreadReply? = null

    val friendlyUrl: String
        get() =
            "/thread/" + id + "/" + title.lowercase(Locale.getDefault())
                .replace("/[^a-z0-9]+/g", "-")
                .replace("/^-+|-+$/g", "-")
                .replace("/^-+|-+$/g", "")
                .replace(" ", "-")

    val formattedBody: String
        get()
        {
            val body: Node = Application.MARKDOWN_PARSER.parse(body)
            return Application.MARKDOWN_RENDERER.render(body)
        }
}