/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop

enum class TransactionStatus {
    PENDING,
    ACTIONS_QUEUED,
    COMPLETED,
    REFUNDED,
    CHARGE_BACK
}