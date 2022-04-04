package dev.foraged.forums.grant.command

import dev.foraged.forums.Application
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.grant.Grant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor

@Service
class GrantCommand @Autowired constructor(val rankRepository: RankRepository, val userRepository: UserRepository) {

    init {
        Application.COMMAND_HANDLER.register(this)
    }

    @Command("grant")
    fun execute(actor: CommandLineActor, name: String, rank: String) {
        val user = userRepository.findByUsername(name)!!
        val rank = rankRepository.findById(rank).get()

        user.addGrant(Grant(
            target = user,
            rank = rank
        ))
        userRepository.save(user)

        actor.reply("permanent grant complete for user $name as rank $rank")
    }
}