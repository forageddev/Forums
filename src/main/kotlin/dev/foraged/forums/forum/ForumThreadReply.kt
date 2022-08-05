package dev.foraged.forums.forum

import com.minexd.core.profile.ProfileService
import dev.foraged.forums.Application
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import org.apache.commons.lang3.RandomStringUtils
import org.commonmark.node.Node
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "thread_replies")
open class ForumThreadReply(
    @Id var id: String = RandomStringUtils.randomAlphanumeric(12),
    var authorId: UUID,
    var body: String = "",

    @DBRef(lazy = true) var thread: ForumThread,
    @DBRef(lazy = true) var category: ForumCategory = thread.category, // forum category is stored in
    @DBRef(lazy = true) var forum: Forum = thread.forum, // forum is stored in
    val timestamp: Long = System.currentTimeMillis(),
)
{
    val author: User = UserRepository.findByIdentifier(authorId)!!

    val friendlyUrl: String
        get() = "/thread/" + thread.id + "/" + thread.title.lowercase(Locale.getDefault())
                .replace("/[^a-z0-9]+/g", "-")
                .replace("/^-+|-+$/g", "-")
                .replace("/^-+|-+$/g", "")
                .replace(" ", "-") + "/" + id

    val formattedBody: String
        get() {
            val safeBody = body
                .replace("<script>", "<script type=\"javascript/blocked\">")
                .replace("<div>", "<bdiv>")
                .replace("</div>", "</bdiv>")

            val body: Node = Application.MARKDOWN_PARSER.parse(safeBody)
            return Application.MARKDOWN_RENDERER.render(body)
        }
}