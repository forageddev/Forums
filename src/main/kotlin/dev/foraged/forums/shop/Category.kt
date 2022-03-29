/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "shop_categories")
class Category(
    @Id val id: String = RandomStringUtils.randomAlphanumeric(8),
    val name: String,
    val order: Int = 0,
    @DBRef val packages: MutableList<Package> = mutableListOf()
)