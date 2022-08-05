package dev.foraged.forums.user.service

import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.store.storage.type.DataStoreStorageType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class UserService : UserDetailsService
{
    val userRepository by lazy {
        UserRepository
    }
    @Autowired lateinit var encoder: BCryptPasswordEncoder

    fun findUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun findUserByRegisterSecret(email: String): User? {
        return userRepository.findByRegisterSecret(email)
    }

    fun findUserByName(name: String): User? {
        return userRepository.findByUsernameIgnoreCase(name)
    }

    fun findUserByUniqueId(uuid: UUID): User? {
        return userRepository.findByIdentifier(uuid)
    }

    fun save(user: User): CompletableFuture<Void> {
        return userRepository.controller.save(user, DataStoreStorageType.MONGO)
    }

    /**
     * Creating registered users for the first time
     *
     * @param user User to register
     */
    @Throws(Exception::class)
    fun createUser(user: User)
    {
        val uniqueId = ScalaStoreUuidCache.uniqueId(user.username) ?: throw RuntimeException("Error finding uniqueId")
        user.identifier = uniqueId
        user.password = encoder.encode(user.password)
        userRepository.controller.save(user)
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
        // make this use other one too
        /* for (grant in user.activeGrants.filterNotNull()) grant.rank.compoundedPermissions.forEach {
            permissions.add(SimpleGrantedAuthority(it))
        }*/
        return permissions
    }

    private fun buildUserForAuthentication(user: User, authorities: List<GrantedAuthority>): UserDetails {
        return org.springframework.security.core.userdetails.User(user.username, user.password, authorities)
    }
}