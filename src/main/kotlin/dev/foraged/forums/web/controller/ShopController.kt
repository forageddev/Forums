/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.web.controller

import dev.foraged.forums.shop.repository.CategoryRepository
import dev.foraged.forums.shop.repository.PackageRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.service.UserService
import me.senta.coinbase.Coinbase
import me.senta.coinbase.body.CreateChargeBody
import me.senta.coinbase.constant.PricingType
import me.senta.coinbase.factory.CoinbaseBuilder
import me.senta.coinbase.models.Price
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import java.math.BigDecimal
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class ShopController @Autowired constructor(val userService : UserService, val categoryRepository: CategoryRepository, val packageRepository: PackageRepository) {

    val coinbase: Coinbase = CoinbaseBuilder().withAPIKey("7c779704-8848-4872-a68b-18c18b8a5b71").build()
    // uh i have to go now you gonna need to either do smt about that or idk what ill look later

    @RequestMapping(value = ["/store", "/shop"], method = [RequestMethod.GET])
    fun home(): ModelAndView {
        val modelAndView = ModelAndView()

        modelAndView.viewName = "shop"
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

    @RequestMapping(value = ["/store/basket/crypto", "/shop/basket/crypto"], method = [RequestMethod.GET])
    fun crypto(response: HttpServletResponse, request: HttpServletRequest): String {
        val user = (request.session.getAttribute("user") as User)

        val body = CreateChargeBody("Nasa Network", "Complete yur payment for play.nasa.gg", PricingType.fixed_price)
        body.localPrice = Price(user.basket.total.toDouble().toString(), "USD")

        return "redirect:" + (coinbase.chargesService.createCharge(body).execute().body()?.hostedUrl ?: "/shop/crypto-failure")
    }

    @RequestMapping(value = ["/store/basket/{category}/{pack}/add", "/shop/basket/{category}/{pack}/add"], method = [RequestMethod.GET])
    fun home(@PathVariable category: String, @PathVariable pack: String, response: HttpServletResponse, request: HttpServletRequest): String {
        val user = (request.session.getAttribute("user") as User)
        user.basket.quantity(packageRepository.findById(pack).get(), 1)
        userService.save(user)
        response.status = 302

        return "redirect:/shop/${category}"
    }

    @RequestMapping(value = ["/store/basket/{category}/{pack}/update", "/shop/basket/{category}/{pack}/update"], method = [RequestMethod.POST])
    fun decrease(@PathVariable category: String, @PathVariable pack: String, response: HttpServletResponse, request: HttpServletRequest): String {
        val user = (request.session.getAttribute("user") as User)
        user.basket.quantity(packageRepository.findById(pack).get(), request.getParameter("quantity").toInt())
        userService.save(user)
        response.status = 302

        return "redirect:/shop/${category}"
    }


    @RequestMapping(value = ["/store/basket/{category}/{pack}/remove", "/shop/basket/{category}/{pack}/remove"], method = [RequestMethod.GET])
    fun remove(@PathVariable category: String, @PathVariable pack: String, response: HttpServletResponse, request: HttpServletRequest): String {
        val user = (request.session.getAttribute("user") as User)
        user.basket.remove(packageRepository.findById(pack).get())
        userService.save(user)
        response.status = 302

        return "redirect:/shop/${category}"
    }
}
