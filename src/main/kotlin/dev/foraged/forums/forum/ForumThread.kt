package dev.foraged.forums.forum

import dev.foraged.forums.Application
import dev.foraged.forums.user.User
import org.commonmark.node.Node
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "threads")
class ForumThread(
    val id: String,
    @DBRef var author: User? = null,
    var title: String = "",
    var body: String = "",

    var forum: String? = null, // forum is stored in
    @DBRef val category: ForumCategory? = null // forum category is stored in
    val timestamp: Long = System.currentTimeMillis(),
    var lastEdited: Long? = null,
    var lastEditedBy: UUID? = null,
    var upvotes: Int = 0,
    var parentThread: String? = null,

)
{
    /**
     * Comments posted on parent thread which are visible
     * when viewing the thread, allows infinite replying as
     * it allows support to comment on replies
     *
     * TODO link to parent thread?
     */
    private val replies: List<ForumThread> = LinkedList() // Changed to link because it's in order

    /**
     * Timestamp on last replied which is updated
     * when a user replies to the parent thread
     *
     * TODO update when a sub-thread is replied to.
     */
    @DBRef
    var lastReply: ForumThread? = null

    // Prevents allowing spaces and stuff
    val friendlyUrl: String
        get() =// Prevents allowing spaces and stuff
            "/thread/" + id + "/" + title.lowercase(Locale.getDefault())
                .replace("/[^a-z0-9]+/g", "-")
                .replace("/^-+|-+$/g", "-")
                .replace("/^-+|-+$/g", "")
                .replace(" ", "-")

    // markdown support
    // todo only allow people with certain permissions to format markdown
    // shut the fuck up queer
    val formattedBody: String
        get()
        {
            val body: Node = Application.MARKDOWN_PARSER.parse(body)
            return Application.MARKDOWN_RENDERER.render(body)
        }
}