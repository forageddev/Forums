package dev.foraged.forums.web.config

import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class PageConfig : WebMvcConfigurer
{
    override fun addViewControllers(registry: ViewControllerRegistry)
    {
        registry.addViewController("/home").setViewName("home")
        registry.addViewController("/").setViewName("home")
        registry.addViewController("/dashboard").setViewName("dashboard")
        registry.addViewController("/login").setViewName("login")
    }

    @Bean
    fun layoutDialect(): LayoutDialect
    {
        return LayoutDialect()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder
    {
        return BCryptPasswordEncoder()
    }
}