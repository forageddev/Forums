/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop

import org.springframework.data.mongodb.core.mapping.DBRef

data class BasketItem(@DBRef var item: Package, var quantity: Int)