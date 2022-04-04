/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop.controller

import com.paypal.core.PayPalEnvironment
import com.paypal.core.PayPalHttpClient
import com.paypal.orders.Order
import com.paypal.orders.OrdersCaptureRequest
import dev.foraged.forums.shop.Basket
import dev.foraged.forums.shop.Transaction
import dev.foraged.forums.shop.TransactionGateway
import dev.foraged.forums.shop.TransactionStatus
import dev.foraged.forums.shop.repository.CategoryRepository
import dev.foraged.forums.shop.repository.PackageRepository
import dev.foraged.forums.shop.repository.TransactionRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import me.senta.coinbase.Coinbase
import me.senta.coinbase.constant.PricingType
import me.senta.coinbase.constant.body.CreateChargeBody
import me.senta.coinbase.services.factory.CoinbaseBuilder
import me.senta.coinbase.services.models.Price
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class ShopController @Autowired constructor(val transactionRepository: TransactionRepository, val userService : UserService, val categoryRepository: CategoryRepository, val packageRepository: PackageRepository) {

    val coinbase: Coinbase = CoinbaseBuilder().withAPIKey("7c779704-8848-4872-a68b-18c18b8a5b71").build()
    val paypal: PayPalHttpClient = PayPalHttpClient(PayPalEnvironment.Live("AV_0lkkcwQf_iwBjZkd2y1ODVNen-klzZ1erVOx7C_tTYfW1UdXGi0uvHctbmEXo0Ljk4AqeMLoCgcrc", "EK9bIjiMmEkTcC5tHjpZiZ_1ic2zF4UyQoSD1K9jhXeSXYLCi4x7mcjb-4g898_a6gVAA0p69uU3kJVW"))

    @RequestMapping(value = ["/guest-login"], method = [RequestMethod.GET])
    fun guestLogin(): ModelAndView {
        val modelAndView = ModelAndView()

        modelAndView.viewName = "guest-login"
        return modelAndView
    }

    @RequestMapping(value = ["/guest-login"], method = [RequestMethod.POST])
    fun guestLogin(@RequestParam username: String, request: HttpServletRequest, response: HttpServletResponse): String {
        if (request.getAttribute("user") != null) {
            throw ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "You are already logged in.")
        }
        val user = userService.findUserByName(username) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A player with that name has never logged on.")

        if (user.registered) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "This account is already registered.")
        }
        response.addCookie(Cookie("guest", user.id.toString()))

        return "redirect:/shop"
    }

    @RequestMapping(value = ["/store", "/shop"], method = [RequestMethod.GET])
    fun home(): ModelAndView {
        val modelAndView = ModelAndView()

        modelAndView.viewName = "shop/index"
        modelAndView.addObject("categories", categoryRepository.findAll())
        return modelAndView
    }

    @RequestMapping(value = ["/store/{category}", "/shop/{category}"], method = [RequestMethod.GET])
    fun home(@PathVariable category: String): ModelAndView {
        val modelAndView = ModelAndView()

        modelAndView.viewName = "shop/packages"

        modelAndView.addObject("categories", categoryRepository.findAll())
        modelAndView.addObject("packages", categoryRepository.findById(category).orElse(null)?.packages)
        modelAndView.addObject("categoryId", category)
        return modelAndView
    }

    @RequestMapping(value = ["/store/payment-pending/{gateway}", "/shop/payment-pending/{gateway}"], method = [RequestMethod.GET])
    fun pending(@PathVariable gateway: String, @RequestParam(name = "id", required = false) id: String?, request: HttpServletRequest): ModelAndView {
        val modelAndView = ModelAndView()
        modelAndView.viewName = "shop/pending"
        modelAndView.addObject("categories", categoryRepository.findAll())

        val way = TransactionGateway.valueOf(gateway.uppercase())

        if (way == TransactionGateway.PAYPAL)
        {
            var order: Order? = null
            try {
                (paypal.execute(OrdersCaptureRequest(id)).result() ?: throw RuntimeException("broke")).also { order = it }
            } catch (ex: Exception) {
                ex.printStackTrace()
                modelAndView.viewName = "redirect:/shop/payment-error?illegal=true"
                return modelAndView
            }
        }
        val user = (request.session.getAttribute("user") as User?) ?: request.session.getAttribute("guest") as User
        val transaction = Transaction(
            basket = user.basket,
            user = user,
            gateway = way
        )
        if (way != TransactionGateway.COINBASE) {
            transaction.status = TransactionStatus.ACTIONS_QUEUED
        }

        transactionRepository.save(transaction)
        user.basket = Basket()
        userService.save(user)

        return modelAndView
    }

    @RequestMapping(value = ["/store/basket/crypto", "/shop/basket/crypto"], method = [RequestMethod.GET])
    fun crypto(response: HttpServletResponse, request: HttpServletRequest): String {
        val user = (request.session.getAttribute("user") as User?) ?: request.session.getAttribute("guest") as User

        val body = CreateChargeBody("Nasa Network", "Complete your payment for play.nasa.gg. Your payment will process under the name ${user.username}", PricingType.fixed_price)
        body.localPrice = Price(user.basket.total.toDouble().toString(), "USD")
        body.metadata = mapOf("transaction_id" to user.basket.id, "uniqueId" to user.id.toString())
        body.redirectUrl = "https://nasa.gg/shop/payment-pending/coinbase"

        return "redirect:" + (coinbase.chargesService.createCharge(body).execute().body()?.hostedUrl ?: "/shop/crypto-failure")
    }

    @RequestMapping(value = ["/store/basket/{category}/{pack}/add", "/shop/basket/{category}/{pack}/add"], method = [RequestMethod.GET])
    fun home(@PathVariable category: String, @PathVariable pack: String, response: HttpServletResponse, request: HttpServletRequest): String {
        val user = (request.session.getAttribute("user") as User?) ?: request.session.getAttribute("guest") as User
        user.basket.quantity(packageRepository.findById(pack).get(), 1)
        userService.save(user)
        response.status = 302

        return "redirect:/shop/${category}"
    }

    @RequestMapping(value = ["/store/basket/{category}/{pack}/update", "/shop/basket/{category}/{pack}/update"], method = [RequestMethod.POST])
    fun decrease(@PathVariable category: String, @PathVariable pack: String, response: HttpServletResponse, request: HttpServletRequest): String {
        val user = (request.session.getAttribute("user") as User?) ?: request.session.getAttribute("guest") as User
        user.basket.quantity(packageRepository.findById(pack).get(), request.getParameter("quantity").toInt())
        userService.save(user)
        response.status = 302

        return "redirect:/shop/${category}"
    }


    @RequestMapping(value = ["/store/basket/{category}/{pack}/remove", "/shop/basket/{category}/{pack}/remove"], method = [RequestMethod.GET])
    fun remove(@PathVariable category: String, @PathVariable pack: String, response: HttpServletResponse, request: HttpServletRequest): String {
        val user = (request.session.getAttribute("user") as User?) ?: request.session.getAttribute("guest") as User
        user.basket.remove(packageRepository.findById(pack).get())
        userService.save(user)
        response.status = 302

        return "redirect:/shop/${category}"
    }
}
