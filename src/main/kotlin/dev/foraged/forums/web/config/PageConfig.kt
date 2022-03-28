package dev.foraged.forums.web.config

import dev.foraged.forums.user.interceptor.UserInterceptorHandler
import nz.net.ultraq.thymeleaf.LayoutDialect
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
        registry.addInterceptor(UserInterceptorHandler()).addPathPatterns(
            "/shop/basket/*", "/store/basket/*", "/thread/create", "/account")
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