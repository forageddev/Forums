package dev.foraged.forums

import com.google.gson.GsonBuilder
import dev.foraged.forums.rank.Rank
import dev.foraged.forums.rank.RankRepository
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import java.util.*

@SpringBootApplication
@ComponentScan("dev.foraged.forums")
open class Application
{
    @Bean
    open fun init(roleRepository: RankRepository): CommandLineRunner
    {
        return CommandLineRunner { args: Array<String?>? ->
            if (!roleRepository.findById("default").isPresent)
            {
                val role = Rank()
                role.id = "default"
                role.name = "Default"
                role.color = "&f"
                role.forumColor = "#ffff"
                role.weight = 0
                roleRepository.save(role)
            }
        }
    }

    companion object
    {
        val RANDOM = Random()
        val MARKDOWN_PARSER = Parser.builder().build()
        val MARKDOWN_RENDERER = HtmlRenderer.builder().build()
        val GSON = GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create()

        lateinit var INSTANCE: Application

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
            INSTANCE = Application()
        }
    }
}