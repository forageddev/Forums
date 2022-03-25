/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop.repository

import dev.foraged.forums.shop.Sale
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SaleRepository : MongoRepository<Sale?, String?>
{
    override fun findAll(): List<Sale?>
    fun getSalesByActive() : List<Sale?>
}