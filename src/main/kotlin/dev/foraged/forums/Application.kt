package dev.foraged.forums

import com.google.gson.GsonBuilder
import dev.foraged.forums.rank.Rank
import dev.foraged.forums.rank.RankRepository
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import revxrsal.commands.cli.ConsoleCommandHandler

@SpringBootApplication
@ComponentScan("dev.foraged.forums")
open class Application
{
    @Bean
    open fun init(roleRepository: RankRepository): CommandLineRunner {
        return CommandLineRunner {
            if (!roleRepository.findById("default").isPresent) roleRepository.save(Rank(id = "default", name = "Default"))
        }
    }

    companion object {
        val COMMAND_HANDLER = ConsoleCommandHandler.create()
        val MARKDOWN_PARSER = Parser.builder().build()
        val MARKDOWN_RENDERER = HtmlRenderer.builder().build()
        val GSON = GsonBuilder()
            .serializeNulls()
            .create()

        lateinit var INSTANCE: Application
        lateinit var CONTEXT: ConfigurableApplicationContext

        @JvmStatic
        fun main(args: Array<String>) {
            CONTEXT = SpringApplication.run(Application::class.java, *args)
            INSTANCE = Application()

            // initialize mail variables
            val properties = System.getProperties()
            properties["mail.smtp.host"] = "smtp.gmail.com"
            properties["mail.smtp.port"] = "465"
            properties["mail.smtp.ssl.enable"] = "true"
            properties["mail.smtp.auth"] = "true"


            COMMAND_HANDLER.pollInput()

        }
    }
}