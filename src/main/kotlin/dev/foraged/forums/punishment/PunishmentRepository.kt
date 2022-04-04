/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.punishment

import dev.foraged.forums.user.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PunishmentRepository : MongoRepository<Punishment?, String?>
{
    fun findPunishmentsByTarget(target: User) : MutableList<Punishment>
}