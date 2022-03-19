package dev.foraged.forums.util

import com.google.gson.internal.LinkedTreeMap
import dev.foraged.forums.rank.Rank
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.user.grant.Grant
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Collectors

@Component
class Utils     //recache();
    (private var rankRepository: RankRepository, private var userRepository: UserRepository)
{
    companion object
    {
        val staffMapCache: MutableMap<Rank, List<User>> = LinkedTreeMap()
        val famousMapCache: MutableMap<Rank, List<User>> = LinkedTreeMap()

        fun recache()
        {
            Companion.rankRepository.findAll().stream().sorted(Comparator.comparingInt { obj: Rank -> obj.weight }
                .reversed()).forEach(Consumer { rank: Rank ->
                if (rank.hasPermission("medusa.staff"))
                {
                    staffMapCache[rank] = Companion.userRepository.findAll().stream().filter(
                        Predicate { user: User -> user.primaryGrant.rankId.equals(rank.id, ignoreCase = true) })
                        .collect<List<User>, Any>(Collectors.toList<User>())
                }
                if (rank.hasPermission("medusa.famous"))
                {
                    famousMapCache[rank] = Companion.userRepository.findAll().stream().filter(
                        Predicate { user: User -> user.primaryGrant.rankId.equals(rank.id, ignoreCase = true) })
                        .collect<List<User>, Any>(Collectors.toList<User>())
                }
            })
        }

        fun convert(string: String): String
        {
            return rank(string)!!.get().hexColor
        }

        fun rank(id: String): Optional<Rank?>?
        {
            return Companion.rankRepository.findById(id)
        }

        fun sort(grants: List<Grant>): List<Grant>
        {
            grants.sort(Comparator.comparingInt { o: Grant ->
                -rank(o.rankId)!!
                    .get().weight
            })
            return grants
        }
    }
}