package dev.foraged.forums.forum

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "forums")
class Forum(@Id val name: String, var weight: Int = 1)
{
    /** List all categories included inside a section  */
    @DBRef
    var categories: MutableList<ForumCategory> = ArrayList() // maybe this should be a linked list?

    val threads: Int get()
        {
            var threads = 0
            for (category in categories) threads += category.threads.size
            return threads
        }
}