package dev.foraged.forums.forum

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "forums")
open class Forum(@Id var name: String, var weight: Int = 1) {
    @DBRef var categories: MutableList<ForumCategory> = mutableListOf()

    val threads: Int get() {
        var threads = 0
        for (category in categories) threads += category.threads.size
        return threads
    }
}


