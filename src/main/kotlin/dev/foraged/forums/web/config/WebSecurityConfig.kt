package dev.foraged.forums.web.config

import dev.foraged.forums.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter()
{
    @Autowired
    private val bCryptPasswordEncoder: BCryptPasswordEncoder? = null

    @Autowired
    var customizeAuthenticationSuccessHandler: CustomizeAuthenticationSuccessHandler? = null
    @Bean
    fun mongoUserDetails(): UserDetailsService
    {
        return UserService()
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder)
    {
        val userDetailsService = mongoUserDetails()
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder)
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity)
    {
        http
            .csrf().ignoringAntMatchers("/api/**").and()
            .authorizeRequests()
            .antMatchers("/**").permitAll() //                .antMatchers("/login").permitAll()
            //                .antMatchers("/register").permitAll()
            //                .antMatchers("/forums/**").permitAll()
            //                .antMatchers("/dashboard/**").permitAll()
            //.hasAuthority("ADMIN")
            .anyRequest()
            .authenticated().and().formLogin().successHandler(customizeAuthenticationSuccessHandler)
            .loginPage("/login").failureUrl("/login?error=true")
            .usernameParameter("username")
            .passwordParameter("password")
            .and().logout()
            .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/").and().exceptionHandling()
    }

    @Throws(Exception::class)
    override fun configure(web: WebSecurity)
    {
        web
            .ignoring()
            .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/img/**")
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource
    {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}