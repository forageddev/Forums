package dev.foraged.forums.rank

import com.google.gson.JsonObject
import dev.foraged.forums.user.User
import dev.foraged.forums.user.UserRepository
import dev.foraged.forums.util.JsonChain
import dev.foraged.forums.util.Utils
import net.minidev.json.JSONArray
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

@Document(collection = "roles")
class Rank(
    @Id val id: String,
    @Indexed(unique = true, direction = IndexDirection.DESCENDING) var name: String? = null,
    var prefix: String = "",
    var color: String = "",
    var hexColor: String = "",
    var weight: Int = -1,
) : Comparable<Rank>
{

    /**
     * Returns permission nodes that a role has, these nodes are given to the user
     * which allows them access to certain features of the forum. They are
     * transformed into GrantedAuthority
     * @see org.springframework.security.core.GrantedAuthority
     */
    private val permissions: MutableList<String> = ArrayList()

    /** Returns roles that are subsidiaries of parent role  */
    @DBRef
    private val inherits: MutableList<Rank> = ArrayList()

    /**
     * Permissions that are from parent role included with child
     * role permissions.
     *
     * @return Permissions from parent and child roles
     */
    val compoundedPermissions: List<String>
        get()
        {
            val toReturn: MutableList<String> = ArrayList(permissions)
            for (inheritedRole in inheritedRoles)
            {
                toReturn.addAll(inheritedRole!!.compoundedPermissions)
            }
            return toReturn
        }

    /**
     * Returns whether a parent role or child of the
     * parent contains a permission node
     *
     * @param node Permission to check
     * @return Whether it contains it or not.
     */
    fun hasPermission(node: String): Boolean
    {
        return permissions.contains(node) || compoundedPermissions.contains(node)
    }

    /**
     * Adds a permission node to the parent role
     *
     * @param node Permission to add
     */
    fun addPermission(node: String)
    {
        permissions.add(node)
    }

    /**
     * Adds a role to the parent role
     *
     * @param id Role id to add
     */
    fun addInherit(id: String)
    {
        Utils.rank(id)!!.ifPresent { rank: Rank -> inherits.add(rank) }
    }

    override fun compareTo(role: Rank): Int {
        return role.weight - this.weight
    }

    fun getUsersWithRank(repository: UserRepository): List<User?> {
        val users = repository.findAll()
        users.removeIf { user: User? -> !user.getPrimaryGrant().rankId.equals(id, ignoreCase = true) }
        return users
    }

    fun toJson() : JsonObject {
        return JsonChain()
            .append("id", id)
            .append("name", name)
            .append("prefix", prefix)
            .append("color", color)
            .append("hexColor", hexColor)
            .append("weight", weight)
            .append("permissions", JSONArray.toJSONString(permissions))
            .append("inherits", JSONArray.toJSONString(inherits.map(Rank::id)))
            .complete()
    }
}