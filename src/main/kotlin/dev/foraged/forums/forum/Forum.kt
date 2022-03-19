package dev.foraged.forums.forum

import lombok.Getter
import lombok.Setter
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Getter
@Setter
@Document(collection = "forums")
class Forum
{
    /** Returns name of forum section  */
    @Id
    private val name: String = null

    /** List all categories included inside a section  */
    @DBRef
    private val categories: List<ForumCategory> = ArrayList()

    /** Weight of the category which will be sorted on listing  */
    private val weight = -1
    val threads: Int
        get()
        {
            var threads = 0
            for (category in categories)
            {
                threads += category.threads.size
            }
            return threads
        }
}