/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop.repository

import dev.foraged.forums.shop.Transaction
import dev.foraged.forums.user.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : MongoRepository<Transaction?, String?>
{
    /** Retrieves a section by it's name, with it outputting  */
    fun getById(name: String?): Transaction?

    fun getTransactionsByUser(user: User) : Transaction?
}