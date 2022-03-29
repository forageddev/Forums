package dev.foraged.forums.web.config

import dev.foraged.forums.Application
import dev.foraged.forums.shop.interceptor.ShopInterceptorHandler
import dev.foraged.forums.user.interceptor.UserLoginInterceptorHandler
import dev.foraged.forums.user.interceptor.UserLoginNotGuestInterceptorHandler
import dev.foraged.forums.user.interceptor.UserLoggedInInterceptorHandler
import dev.foraged.forums.user.service.UserService
import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class PageConfig : WebMvcConfigurer
{
    override fun addViewControllers(registry: ViewControllerRegistry)
    {
        registry.addViewController("/home").setViewName("home")
        registry.addViewController("/").setViewName("home")
        registry.addViewController("/dashboard").setViewName("dashboard")
        registry.addViewController("/login").setViewName("login")
    }

    override fun addInterceptors(registry: InterceptorRegistry)
    {
        registry.addInterceptor(UserLoginNotGuestInterceptorHandler()).addPathPatterns(
            "/thread/create", "/thread/reply", "/account")
        registry.addInterceptor(UserLoginInterceptorHandler()).addPathPatterns(
            "/shop/basket/*", "/store/basket/*")
        registry.addInterceptor(UserLoggedInInterceptorHandler()).addPathPatterns(
            "/register", "/login", "/guest-login")
        registry.addInterceptor(ShopInterceptorHandler()).addPathPatterns("/shop/**", "/store/**")
    }

    @Bean
    open fun layoutDialect(): LayoutDialect
    {
        return LayoutDialect()
    }

    @Bean
    open fun passwordEncoder(): BCryptPasswordEncoder
    {
        return BCryptPasswordEncoder()
    }
}