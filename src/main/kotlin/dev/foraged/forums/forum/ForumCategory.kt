package dev.foraged.forums.forum

import lombok.*
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Getter
@Document(collection = "categories")
@NoArgsConstructor
@RequiredArgsConstructor
class ForumCategory
{
    /** Returns name for forum category  */
    @Setter
    private val id: String = null

    /** Returns the display name for forum category  */
    @Setter
    private val displayName: String = null

    /** Returns the forum it's located in  */
    @Setter
    @DBRef
    private val forum: Forum = null

    /** Returns the description of a category  */
    @Setter
    private val description = ""

    /** List all threads included inside a category  */
    @DBRef val threads: Set<ForumThread> =
        HashSet() // Changed to only include identifiers as Threads are stored in different repo

    /** Weight of the category which will be sorted on listing  */
    @Setter
    private val weight = -1

    /**
     * Timestamp on last modified which is updated
     * when a user creates or replies to a category
     */
    @Setter
    private val lastActivity: Long = -1

    /**
     * Permission node required to view a categories' threads,
     * if empty or null, it'll be viewable by anyone including non-registered
     * users.
     */
    @Setter
    private val permission = ""

    // Prevents allowing spaces and stuff
    val friendlyUrl: String
        get() =// Prevents allowing spaces and stuff
            "/forums/" + this.getId()
}