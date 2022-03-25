package dev.foraged.forums.user.command

import dev.foraged.forums.Application
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.util.MojangUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor
import revxrsal.commands.exception.CommandErrorException

@Service
class UserCreateCommand @Autowired constructor(val rankRepository: RankRepository, val userRepository: UserRepository) {

    init {
        Application.COMMAND_HANDLER.register(this)
    }

    @Command("user create")
    fun execute(actor: CommandLineActor, name: String) {
        var user = userRepository.findByUsername(name)
        if (user != null) throw CommandErrorException("Retard that kid already exists ify ou try to do that agani i'm gonna swat you")

        user = User(
            id = MojangUtils.fetchUUID(name)!!,
            username = name
        )
        userRepository.save(user)

        actor.reply("done")
    }
}