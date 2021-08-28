package gg.manny.forums.rank;

import com.google.gson.JsonObject;
import gg.manny.forums.user.User;
import gg.manny.forums.user.UserRepository;
import gg.manny.forums.util.CC;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Document(collection = "roles")
public class Rank implements Comparable<Rank> {

    @Autowired private MongoTemplate mongoTemplate;

    /** Returns the id of a role, it doesn't include spaces or capitalization **/
    @Id @NonNull @Setter private String id;

    /** Returns the display name of a role */
    @Indexed(unique = true, direction = IndexDirection.DESCENDING)
    @Setter private String name;

    /** Returns the prefix of a role */
    @Setter private String prefix = "";

    /** Returns the color of a role, it uses chatcolor for bukkit */
    @Setter private String color = "&f";

    /** Returns the color of a role, it uses hex color codes otherwise will return without a colour */
    @Setter private String forumColor = "#ffff";

    /** Returns the weight of a role, this sorts roles into hierarchy order */
    @Setter private int weight = -1;

    /**
     * Returns permission nodes that a role has, these nodes are given to the user
     * which allows them access to certain features of the forum. They are
     * transformed into GrantedAuthority
     * @see org.springframework.security.core.GrantedAuthority
     */
    private List<String> permissions = new ArrayList<>();

    /** Returns roles that are subsidiaries of parent role */
    @DBRef private List<Rank> inherits = new ArrayList<>();

    /**
     * Display name that contains the name of the role and the color
     *
     * @return Coloured display name
     */
    public String getDisplayName() {
        return getColor() + name;
    }

    /**
     * Permissions that are from parent role included with child
     * role permissions.
     *
     * @return Permissions from parent and child roles
     */
    public List<String> getCompoundedPermissions() {
        List<String> toReturn = new ArrayList<>(this.permissions);
        for (Rank inheritedRole : getInheritedRoles()) {
            toReturn.addAll(inheritedRole.getCompoundedPermissions());
        }
        return toReturn;
    }

    /**
     * Returns whether a parent role or child of the
     * parent contains a permission node
     *
     * @param node Permission to check
     * @return Whether it contains it or not.
     */
    public boolean hasPermission(String node) {
        return permissions.contains(node) || getCompoundedPermissions().contains(node);
    }

    /**
     * Adds a permission node to the parent role
     *
     * @param node Permission to add
     */
    public void addPermission(String node) {
        permissions.add(node);
    }

    public List<Rank> getInheritedRoles() {
        return inherits;
    }

    @Override
    public int compareTo(Rank role) {
        return role.getWeight() - this.getWeight();
    }

    public List<User> getUsersWithRank(UserRepository repository) {
        List<User> users = repository.findAll();
        users.removeIf(user -> !user.getPrimaryGrant().getRankId().equalsIgnoreCase(id));
        return users;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("name", name);
        object.addProperty("prefix", prefix);
        object.addProperty("color", color);
        object.addProperty("forumColor", forumColor);
        object.addProperty("weight", weight);
        object.addProperty("permissions", JSONArray.toJSONString(permissions));
        return object;
    }
}