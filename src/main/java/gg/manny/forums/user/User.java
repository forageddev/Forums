package gg.manny.forums.user;

import com.google.common.collect.Lists;
import gg.manny.forums.Application;
import gg.manny.forums.rank.Rank;
import gg.manny.forums.rank.RankRepository;
import gg.manny.forums.user.grant.Grant;
import gg.manny.forums.user.punishment.Punishment;
import gg.manny.forums.util.CC;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Document(collection = "users")
public class User {

    @Autowired private RankRepository repository;

    /** Returns the unique identifer for a user **/
    @Setter @Id private UUID id;

    /** Returns the unique name of a user */
    @Indexed(unique = true, direction = IndexDirection.DESCENDING)
    @Getter @Setter private String username;

    /** Returns the email address of a user */
    @Getter @Setter private String email;

    /** Returns an encrpyted (hash + salted) password  */
    @Getter @Setter private String password;

    /** Returns whether a user is registered or not */
    @Setter private boolean registered = false;

    /** Returns whether a user is online or not */
    @Setter private boolean online = false;

    /** Returns all existing grants of a user including inactives one */
    private List<Grant> grants = new ArrayList<>();

    /** Returns all existing punishments of a user including inactives for historical purposes */
    private List<Punishment> punishments = new ArrayList<>();

    /** Returns the date upon the first registration of a user */
    @Setter private Date dateJoined;

    /** Returns the date upon the last login of a user */
    @Setter private Date dateLastSeen; // todo add a system to check if they are online or not

    /** Returns a map of ip addresses and date used */
    private Map<Date, String> ipAddresses = new HashMap<>();

    private Map<String, Object> metaData = new HashMap<>(); // todo future use for external systems storing data

    public void addGrant(Grant grant) {
        grants.add(grant);
    }

    public void addPunishment(Punishment punishment) {
        punishments.add(punishment);
    }

    // Todo get their active role -- or default when not active
    // todo add their active grant and prevent inactive (temporarily ones) from being active
    public List<Grant> getActiveGrants() {
        List<Grant> activeGrants = Lists.newArrayList();
        for (Grant grant : getGrants()) if (!grant.isActive()) activeGrants.add(grant);
        return activeGrants;
    }

    public String getRankColor() {
        return CC.convert(getPrimaryGrant().getRankId());
    }

    public Grant getPrimaryGrant() {
        for (Grant grant : getGrants()) {
            if (grant.isActive()) {
                return grant;
            }
        }
        Grant grant = new Grant();
        grant.setRankId("Default");
        return grant;
    }

    public boolean hasPermission(String permission) {
        Rank rank = repository.findById(getPrimaryGrant().getRankId()).orElse(null);
        if (rank == null) return false;
        return rank.hasPermission(permission);

    }

    public String getLastServer() {
        return "DEV-01";
    }
}
