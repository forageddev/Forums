package dev.foraged.forums.user.service

import dev.foraged.forums.rank.Rank
import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.util.MojangUtils
import dev.foraged.forums.util.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Consumer

@Service
class UserService : UserDetailsService
{
    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    val roleRepository: RankRepository? = null

    @Autowired
    private val encoder: BCryptPasswordEncoder? = null
    fun findUserByEmail(email: String?): User?
    {
        return userRepository!!.findByEmail(email)
    }

    fun findUserByName(name: String?): User?
    {
        return userRepository!!.findByUsernameIgnoreCase(name)
    }

    fun save(user: User): User
    {
        return userRepository!!.save(user)
    }

    /**
     * Creating registered users for the first time
     *
     * @param user User to register
     */
    @Throws(Exception::class)
    fun createUser(user: User?)
    {
        val uniqueId = MojangUtils.fetchUUID(user.getUsername()) ?: throw RuntimeException("Error finding uniqueId")
        val existingUser = userRepository!!.findById(uniqueId).orElse(null)
        user.setId(uniqueId)
        user.setDateJoined(Date(System.currentTimeMillis()))
        user.setDateLastSeen(Date(System.currentTimeMillis()))
        user.setPassword(encoder!!.encode(user.getPassword()))
        user.setRegistered(true) // todo add confirmations by email
        if (existingUser != null)
        {
            user.getGrants().addAll(existingUser.grants)
            user.getPunishments().addAll(existingUser.punishments)
        }
        userRepository.save(user)
    }

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails
    {
        val user =
            if (username.contains("@")) userRepository!!.findByEmail(username) else userRepository!!.findByUsername(
                username
            )
        return if (user != null)
        {
            println("USER FROUND" + user.username)
            val authorities = getUserAuthority(user)
            buildUserForAuthentication(user, authorities)
        } else
        {
            throw UsernameNotFoundException("Username not found")
        }
    }

    private fun getUserAuthority(user: User): List<GrantedAuthority>
    {
        val permissions: MutableList<GrantedAuthority> = ArrayList()
        for (grant in user.activeGrants)
        {
            val rank: Optional<Rank?> = Utils.Companion.rank(grant.rankId)
            rank.ifPresent { value: Rank? ->
                value.getCompoundedPermissions().forEach(
                    Consumer { node: String? -> permissions.add(SimpleGrantedAuthority(node)) })
            }
        }
        return permissions
    }

    private fun buildUserForAuthentication(user: User, authorities: List<GrantedAuthority>): UserDetails
    {
        return org.springframework.security.core.userdetails.User(user.username, user.password, authorities)
    }
}