package dev.foraged.forums.user.service

import dev.foraged.forums.rank.RankRepository
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.util.MojangUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService : UserDetailsService
{
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var roleRepository: RankRepository
    @Autowired lateinit var encoder: BCryptPasswordEncoder

    fun findUserByEmail(email: String?): User?
    {
        return userRepository.findByEmail(email)
    }

    fun findUserByRegisterSecret(email: String): User? {
        return userRepository.findByRegisterSecret(email)
    }

    fun findUserByName(name: String?): User? {
        return userRepository.findByUsernameIgnoreCase(name)
    }

    fun save(user: User): User
    {
        return userRepository.save(user)
    }

    /**
     * Creating registered users for the first time
     *
     * @param user User to register
     */
    @Throws(Exception::class)
    fun createUser(user: User)
    {
        val uniqueId = MojangUtils.fetchUUID(user.username) ?: throw RuntimeException("Error finding uniqueId")
        val existingUser = userRepository!!.findById(uniqueId).orElse(null)
        user.id = uniqueId
        user.password = encoder.encode(user.password)

        if (existingUser != null) {
            user.grants.addAll(existingUser.grants)
            user.punishments.addAll(existingUser.punishments)
        }
        userRepository.save(user)
    }

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails
    {
        val user =
            if (username.contains("@")) userRepository.findByEmail(username) else userRepository.findByUsername(
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
        for (grant in user.activeGrants.filterNotNull()) grant.rank.compoundedPermissions.forEach {
            permissions.add(SimpleGrantedAuthority(it))
        }
        return permissions
    }

    private fun buildUserForAuthentication(user: User, authorities: List<GrantedAuthority>): UserDetails {
        return org.springframework.security.core.userdetails.User(user.username, user.password, authorities)
    }
}