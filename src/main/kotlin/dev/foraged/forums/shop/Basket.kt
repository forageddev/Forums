/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop

import java.math.BigDecimal

class Basket(
    var items: MutableList<BasketItem> = mutableListOf()
) {
    val total: BigDecimal get() {
        var total = BigDecimal.ZERO
        items.forEach {
            total = total.add(it.item.price.multiply(BigDecimal(it.quantity)))
        }
        return total
    }

    fun size() : Int {
        return items.size
    }

    fun quantity(pack: Package, amount: Int) {
        if (amount != 0 && items.any { it.item == pack }) items.find { it.item == pack }!!.quantity = amount
        else if (amount == 0) items.removeIf { it.item == pack }
        else items.add(BasketItem(pack, 1))
    }

    fun isEmpty() : Boolean {
        return items.isEmpty()
    }

    fun remove(pack: Package) {
        items.removeIf {
            it.item == pack
        }
    }
}