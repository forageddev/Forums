/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.rank.adapter

import com.google.gson.*
import dev.foraged.forums.Application
import dev.foraged.forums.rank.Rank
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.util.JsonChain
import java.lang.reflect.Type

class RankReferenceJsonAdapter : JsonSerializer<Rank>, JsonDeserializer<Rank>
{
    override fun serialize(rank: Rank?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement
    {
        return JsonChain().append("id", rank!!.id).complete()
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Rank
    {
        return Application.CONTEXT.beanFactory.getBean(RankRepository::class.java).findById(json!!.asString).get()
    }
}