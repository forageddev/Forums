/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop

import dev.foraged.forums.user.User
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "transactions")
class Transaction(
    val basket: Basket,
    @Id val id: String = basket.id,
    @DBRef val user: User,
    var status: TransactionStatus = TransactionStatus.PENDING,
    val gateway: TransactionGateway,
    val timestamp: Long = System.currentTimeMillis()
)
{
}