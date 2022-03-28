/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop

import org.apache.catalina.User
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "transactions")
class Transaction(
    @Id val id: String = RandomStringUtils.randomAlphanumeric(16),
    @DBRef val user: User,
    val basket: Basket,
    var status: TransactionStatus,
    val gateway: TransactionGateway,
    val timestamp: Long = System.currentTimeMillis()
)
{
}