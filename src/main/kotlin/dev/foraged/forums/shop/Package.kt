/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection = "shop_packages")
class Package(
    @Id val id: String = RandomStringUtils.randomAlphanumeric(8),
    val name: String,
    val price: BigDecimal,
    @DBRef val sale: Sale? = null
) : Comparable<Package>
{
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Package -> { id == other.id }
            is String -> { id == other }
            else -> super.equals(other)
        }
    }

    override fun compareTo(other: Package): Int
    {
        return (price.toDouble() - other.price.toDouble()).toInt()
    }

    override fun hashCode(): Int
    {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + (sale?.hashCode() ?: 0)
        return result
    }
}