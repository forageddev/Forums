/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.grant

import dev.foraged.forums.user.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface GrantRepository : MongoRepository<Grant?, String?>
{
    fun findGrantsByTarget(target: User) : MutableList<Grant>
}