package dev.foraged.forums.filter

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "filter")
data class Filter(@Id val id: String, @Indexed(unique = true, direction = IndexDirection.DESCENDING) var content: String = "nigger")