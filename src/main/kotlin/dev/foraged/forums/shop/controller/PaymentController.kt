/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop.controller

import com.google.gson.JsonObject
import dev.foraged.forums.Application
import dev.foraged.forums.shop.Basket
import dev.foraged.forums.shop.Transaction
import dev.foraged.forums.shop.TransactionStatus
import dev.foraged.forums.shop.repository.TransactionRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import me.senta.coinbase.constant.PricingType
import me.senta.coinbase.constant.body.CreateChargeBody
import me.senta.coinbase.services.models.Price
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api/v1/payment")
class PaymentController @Autowired constructor(val userService: UserService, val transactionRepository: TransactionRepository)
{
    @RequestMapping(value = ["/crypto/verify", "/crypto/verify"], method = [RequestMethod.POST])
    fun tracker(@RequestBody raw: String, response: HttpServletResponse, request: HttpServletRequest): String {
        val body = Application.GSON.fromJson(raw, JsonObject::class.java)
        val data = body["event"].asJsonObject
        val meta = data["metadata"].asJsonObject

        if (meta.has("transaction_id") && meta.has("uniqueId"))
        {
            val id = meta["transaction_id"].asString

            val user = userService.findUserByUniqueId(UUID.fromString(meta["uniqueId"].asString))

            if (user != null)
            {
                val transaction = transactionRepository.getById(id)

                if (transaction != null)
                {
                    transaction.status = TransactionStatus.ACTIONS_QUEUED
                    transactionRepository.save(transaction)
                } else return "transaction not found"
            } else return "invalid user"
        }
        return "missing meta data"
    }
}