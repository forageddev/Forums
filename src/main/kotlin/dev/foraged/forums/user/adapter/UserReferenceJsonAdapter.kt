/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.user.adapter

import com.google.gson.*
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.util.JsonChain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.reflect.Type
import java.util.*

@Component
class UserReferenceJsonAdapter : JsonSerializer<User>, JsonDeserializer<User>
{
    @Autowired lateinit var repository: UserRepository

    override fun serialize(user: User?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement
    {
        return JsonChain().append("id", user!!.id.toString()).complete()
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): User
    {
        return repository.findById(UUID.fromString(json!!.asJsonObject["id"].asString)).get()
    }
}