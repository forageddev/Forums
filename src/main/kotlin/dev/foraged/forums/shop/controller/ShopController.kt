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
import dev.foraged.forums.Application
import dev.foraged.forums.shop.*
import dev.foraged.forums.shop.repository.CategoryRepository
import dev.foraged.forums.shop.repository.PackageRepository
import dev.foraged.forums.shop.repository.TransactionRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import dev.foraged.shop.purchase.PackageAction
import dev.foraged.shop.purchase.PackageActionType
import me.senta.coinbase.Coinbase
import me.senta.coinbase.constant.PricingType
import me.senta.coinbase.body.CreateChargeBody
import me.senta.coinbase.factory.CoinbaseBuilder
import me.senta.coinbase.models.Price
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor
import java.math.BigDecimal
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller("/shop")
class ShopController @Autowired constructor(
    val transactionRepository: TransactionRepository,
    val userService : UserService,
    val categoryRepository: CategoryRepository,
    val packageRepository: PackageRepository
) {

    val coinbase: Coinbase = CoinbaseBuilder().withAPIKey("55595e44-ac31-401a-95fd-dde7b4714d8e").build()
    val paypal: PayPalHttpClient = PayPalHttpClient(PayPalEnvironment.Live(
        "AbDEJtSnoj1ADUA99_1SOAo7F1959wJVEarI-EyZ4BqRHxDj42zt6nyw3-sdhMUPa-nEAvn_Vvhr71RQ",
        "ELooXhwru0mI8bv4ivYrY6VHBA55b_8HNHrvbuuoz0UoW4farP2uYTNlbuRY9GNWLNAo04J_1CmHoje6")
    )

    init {
        Application.COMMAND_HANDLER.register(this)
    }

    @Command("shop cg")
    fun cg(actor: CommandLineActor, name: String) {
        categoryRepository.save(Category(name = name))
        actor.reply("done")
    }

    @Command("shop cp")
    fun cg(actor: CommandLineActor, price: Double, name: String) {
        packageRepository.save(Package(name = name, price = BigDecimal(price)))
        actor.reply("done")
    }

    @Command("shop pa")
    fun pa(actor: CommandLineActor, name: String, target: String, action: String, data: String) {
        val pck = packageRepository.findById(name).orElse(null) ?: throw RuntimeException("pack retard")

        pck.actions.add(PackageAction(target, data, PackageActionType.valueOf(action), false))
        packageRepository.save(pck)
        actor.reply("done")
    }

    @Command("shop lp")
    fun cg(actor: CommandLineActor, name: String, category: String) {
        val pck = packageRepository.findById(name).orElse(null) ?: throw RuntimeException("pack retard")
        val category = categoryRepository.getByName(category) ?: throw RuntimeException("catreatd")

        category.packages.add(pck)
        categoryRepository.save(category)
        actor.reply("done")
    }

    @Command("shop fs")
    fun fs(actor: CommandLineActor) {
        transactionRepository.findAll().filterNotNull().filter { it.status == TransactionStatus.ACTIONS_QUEUED }.forEach {
            it.execute()
            actor.reply("Executed ${it.id}")
        }
    }

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
        response.addCookie(Cookie("guest", user.identifier.toString()))

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
        modelAndView.addObject("category", categoryRepository.findById(category).orElse(null))
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
            user = user.identifier,
            gateway = way
        )
        if (way != TransactionGateway.COINBASE) {
            transaction.status = TransactionStatus.ACTIONS_QUEUED
            transaction.execute()
        }

        transactionRepository.save(transaction)
        user.basket = Basket()
        userService.save(user)

        return modelAndView
    }

    @RequestMapping(value = ["/store/basket/crypto", "/shop/basket/crypto"], method = [RequestMethod.GET])
    fun crypto(response: HttpServletResponse, request: HttpServletRequest): String {
        val user = (request.session.getAttribute("user") as User?) ?: request.session.getAttribute("guest") as User

        val body = CreateChargeBody("Nyte Network", "Complete your payment for play.nyte.cc. Your payment will process under the name ${user.username}", PricingType.fixed_price)
        body.localPrice = Price(user.basket.total.toDouble().toString(), "USD")
        body.metadata = mapOf("transaction_id" to user.basket.id, "uniqueId" to user.identifier.toString())
        body.redirectUrl = "https://nyte.cc/shop/payment-pending/coinbase"

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
