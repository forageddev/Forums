/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop.repository

import dev.foraged.forums.shop.Package
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PackageRepository : MongoRepository<Package?, String?>
{
    /** Retrieves a section by it's name, with it outputting  */
    fun getByName(name: String?): Package?
    // We gotta save all threads so we can fetch instead of querying each created forum
    /** List all forum threads  */
    override fun findAll(): List<Package?>
}