package dev.foraged.forums.forum

import dev.foraged.forums.Application
import dev.foraged.forums.user.User
import org.apache.commons.lang3.RandomStringUtils
import org.commonmark.node.Node
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "thread_replies")
class ForumThreadReply(
    val id: String = RandomStringUtils.randomAlphanumeric(12),
    @DBRef var author: User? = null,
    var body: String = "",

    @DBRef var thread: ForumThread,
    @DBRef var category: ForumCategory = thread.category, // forum category is stored in
    @DBRef var forum: Forum = thread.forum, // forum is stored in
    val timestamp: Long = System.currentTimeMillis(),
    )
{
    // Prevents allowing spaces and stuff
    val friendlyUrl: String
        get() =// Prevents allowing spaces and stuff
            "/thread/" + thread.id + "/" + thread.title.lowercase(Locale.getDefault())
                .replace("/[^a-z0-9]+/g", "-")
                .replace("/^-+|-+$/g", "-")
                .replace("/^-+|-+$/g", "")
                .replace(" ", "-") + "/" + id

    // markdown support
    // todo only allow people with certain permissions to format markdown
    val formattedBody: String
        get()
        {
            val body: Node = Application.MARKDOWN_PARSER.parse(body)
            return Application.MARKDOWN_RENDERER.render(body)
        }
}