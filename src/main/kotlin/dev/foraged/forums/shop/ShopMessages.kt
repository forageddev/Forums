package dev.foraged.forums.shop

import dev.foraged.forums.shop.repository.TransactionRepository
import dev.foraged.shop.ShopShared
import dev.foraged.shop.purchase.PurchaseInfo
import gg.scala.aware.annotation.Subscribe
import gg.scala.aware.message.AwareMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShopMessages
{
    @Autowired lateinit var transactionRepository: TransactionRepository

    @Subscribe("ShopMessageUpdate")
    fun shopUpdate(message: AwareMessage) {
        val info = message.retrieve<PurchaseInfo>("info")
        val transaction = transactionRepository.getById(info.transactionId) ?: throw RuntimeException("STORE RETURNED AN ERROR BUT COULDN'T FIND TRANSACTION WITH ID ${info.transactionId}")
        if (info.items.isEmpty()) {
            transaction.status = TransactionStatus.COMPLETED
            transactionRepository.save(transaction)
            return
        }

        println("Debug Shop:")
        info.items.forEach {
            println("Not executed: ${it.quantity}x ${it.name}")
        }
        transaction.queuedItems.removeIf {
            println("Removing ${it.item.name} from queued actions for ${info.transactionId} if not in sent back items")
            it.item.name !in info.items.map { it.name }
        }
    }
}