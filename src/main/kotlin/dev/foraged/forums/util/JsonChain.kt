/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.util

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*

class JsonChain
{
    private val json: JsonObject = JsonObject()

    fun append(key: String, value: Boolean?) : JsonChain
    {
        json.addProperty(key, value)
        return this
    }

    fun append(key: String, value: Number?) : JsonChain
    {
        json.addProperty(key, value)
        return this
    }

    fun append(key: String, value: String?) : JsonChain
    {
        if (value == "null") json.add(key, null)
        else json.addProperty(key, value)
        return this
    }

    fun append(key: String, value: Char?) : JsonChain
    {
        json.addProperty(key, value)
        return this
    }

    fun append(key: String, value: UUID?) : JsonChain
    {
        if (value == null) json.add(key, null)
        else json.addProperty(key, value.toString())
        return this
    }

    fun append(key: String, value: JsonElement) : JsonChain
    {
        json.add(key, value)
        return this
    }

    fun complete(): JsonObject
    {
        return json

    }
}