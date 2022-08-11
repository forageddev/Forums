package dev.foraged.forums

import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.minexd.core.CoreShared
import com.minexd.core.plugin.Plugin
import com.minexd.core.plugin.PluginEventHandler
import com.minexd.core.profile.Profile
import com.minexd.core.profile.ProfileService
import com.minexd.core.profile.grant.Grant
import com.minexd.core.profile.punishment.Punishment
import com.minexd.core.rank.Rank
import dev.foraged.commons.CommonsShared
import dev.foraged.forums.leaderboard.LeaderboardService
import dev.foraged.forums.profile.SiteProfile
import dev.foraged.forums.shop.ShopMessages
import dev.foraged.shop.ShopShared
import gg.scala.aware.AwareHub
import gg.scala.aware.uri.WrappedAwareUri
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.details.DataStoreMongoConnectionDetails
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.DataStoreRedisConnection
import gg.scala.store.serializer.serializers.GsonSerializer
import net.evilblock.cubed.serializers.Serializers
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import revxrsal.commands.cli.ConsoleCommandHandler
import java.lang.reflect.Type
import java.util.*
import java.util.logging.Logger

@SpringBootApplication
@ComponentScan("dev.foraged.forums")
open class Application : ScalaDataStoreShared(), Plugin, PluginEventHandler
{
    override fun getProfileType(): Type { return Profile::class.java }
    override fun getLogger(): Logger { return Logger.getAnonymousLogger() }
    override fun createProfileInstance(uuid: UUID) = SiteProfile(uuid)
    override fun getEventHandler(): PluginEventHandler { return this }
    override fun getActiveGroups(): Set<String> { return setOf("GLOBAL") }
    override fun callGrantUpdateEvent(target: Profile, grant: Grant)
    {
        TODO("Not yet implemented")
    }

    override fun callPunishmentUpdateEvent(target: Profile, punishment: Punishment)
    {
        TODO("Not yet implemented")
    }

    override fun callRankUpdateEvent(rank: Rank)
    {
        TODO("Not yet implemented")
    }

    override fun getNewRedisConnection(): AbstractDataStoreRedisConnection {
        return DataStoreRedisConnection()
    }

    override fun debug(from: String, message: String) {
        getLogger().info("[${from}] [debug] $message")
    }

    override fun getNewMongoConnection(): AbstractDataStoreMongoConnection {
        return UriDataStoreMongoConnection(DataStoreMongoConnectionDetails("mongodb://localhost:27017/Scala", "Scala"))
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
            AwareHub.configure(WrappedAwareUri()) {
                GSON!!
            }
            ScalaDataStoreShared.INSTANCE = INSTANCE
            Serializers.useGsonBuilderThenRebuild {
                it.registerTypeAdapter(Profile::class.java, object : InstanceCreator<Profile> {
                    override fun createInstance(p0: Type): Profile {
                        return SiteProfile(UUID.randomUUID())
                    }

                }).create()
            }
            GsonSerializer.provideCustomGson {
                Serializers.gson
            }
            CoreShared(INSTANCE).configure()
            CommonsShared.configure()
            ShopShared().configure(CONTEXT.getBean(ShopMessages::class.java))
            LeaderboardService.configure()

            COMMAND_HANDLER.pollInput()
        }
    }
}