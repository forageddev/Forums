package dev.foraged.forums.user.command

import dev.foraged.forums.Application
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.store.storage.type.DataStoreStorageType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor
import revxrsal.commands.exception.CommandErrorException

@Service
class UserCreateCommand {


    @Autowired lateinit var encoder: BCryptPasswordEncoder

    init {
        Application.COMMAND_HANDLER.register(this)
    }

    @Command("serialize")
    fun serialize(actor: CommandLineActor, text: String) {
        actor.reply(encoder.encode(text))
    }

    @Command("user create")
    fun execute(actor: CommandLineActor, name: String) {
        var user = UserRepository.findByUsername(name)
        if (user != null) throw CommandErrorException("Retard that kid already exists ify ou try to do that agani i'm gonna swat you")

        user = User(
            identifier = ScalaStoreUuidCache.uniqueId(name)!!,
            username = name
        )
        UserRepository.controller.save(user, DataStoreStorageType.MONGO)

        actor.reply("done")
    }
}