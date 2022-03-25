package dev.foraged.forums.rank.command

import dev.foraged.forums.Application
import dev.foraged.forums.rank.Rank
import dev.foraged.forums.rank.RankRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import revxrsal.commands.annotation.Command
import revxrsal.commands.cli.core.CommandLineActor

@Service
class RankCreateCommand @Autowired constructor(val rankRepository: RankRepository) {

    init {
        Application.COMMAND_HANDLER.register(this)
    }

    @Command("rank create")
    fun execute(actor: CommandLineActor, name: String) {
        val rank = Rank(
            id = name.lowercase(),
            name = name
        )
        rank.permissions.add("minecraft.staff")
        rankRepository.save(rank)
        actor.reply("Setup a staff rank for testing purposes")
    }
}