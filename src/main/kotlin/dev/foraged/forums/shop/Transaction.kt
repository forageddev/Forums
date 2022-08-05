/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop

import com.minexd.core.CoreShared
import com.minexd.core.profile.ProfileService
import com.minexd.core.profile.grant.Grant
import com.minexd.core.profile.grant.GrantUpdate
import com.minexd.core.rank.RankService
import dev.foraged.shop.ShopShared
import dev.foraged.shop.purchase.PackageActionType
import dev.foraged.shop.purchase.PurchaseDetails
import dev.foraged.shop.purchase.PurchaseInfo
import gg.scala.aware.message.AwareMessage
import net.evilblock.cubed.serializers.Serializers
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "transactions")
class Transaction(
    val basket: Basket,
    @Id val id: String = basket.id,
    val user: UUID,
    var queuedItems: MutableList<BasketItem> = basket.items,
    var status: TransactionStatus = TransactionStatus.PENDING,
    val gateway: TransactionGateway,
    val timestamp: Long = System.currentTimeMillis()
)
{
    fun execute() {
        val info = PurchaseInfo(user, id, mutableListOf())
        queuedItems.forEach {
            if (it.item.actions.first().type == PackageActionType.GRANT) {
                val profile = ProfileService.fetchProfile(user)

                if (!profile.grants.any { it.reason.contains(" - ") && it.reason.split(" - ")[1] == id })
                {
                    AwareMessage.of(
                        "GrantUpdate", CoreShared.instance.aware,
                        "TargetID" to user,
                        "Grant" to Grant(
                            RankService.getRankById(UUID.fromString(it.item.actions.first().data)),
                            issuedBy = null,
                            reason = "Store Purchase - $id",
                            expiresAt = null
                        ),
                        "Update" to GrantUpdate.CREATE
                    ).publish()
                }
            }

            info.items.add(PurchaseDetails(it.item.name, it.quantity, it.item.actions))
        }
        AwareMessage.of("ShopMessageInfo", ShopShared.instance.aware,
            "info" to Serializers.gson.toJson(info)
        ).publish()
    }
}