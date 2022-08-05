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
import org.springframework.stereotype.Component
import java.lang.reflect.Type
import java.util.*

@Component
class UserReferenceJsonAdapter : JsonSerializer<User>, JsonDeserializer<User>
{
    override fun serialize(user: User, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement
    {
        return JsonChain().append("id", user.identifier.toString()).complete()
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): User
    {
        return UserRepository.findByIdentifier(UUID.fromString(json.asJsonObject["id"].asString)) ?: throw JsonParseException("User not found")
    }
}