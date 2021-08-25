package gg.manny.forums.user.grant;

import com.google.gson.JsonObject;
import gg.manny.forums.rank.Rank;
import gg.manny.forums.rank.RankRepository;
import gg.manny.forums.user.UserRepository;
import gg.manny.forums.user.service.UserService;
import gg.manny.forums.util.MojangUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Grant implements Comparable<Grant> {

    @Autowired private RankRepository repository;
    @Autowired private UserRepository userRepository;

    @NonNull private UUID id;
    @NonNull private UUID target;

    private String rankId; // Used for POST data to convert id into rank (limitations of Springboot)
    @DBRef @NonNull private Rank rank;

    public String reason;

    private UUID issuedBy;
    private long issuedAt = System.currentTimeMillis();

    private Long duration;

    private String removalReason;
    private UUID removedBy;
    private Long removedAt;

    public Grant(JsonObject object, UUID target, Rank rank) {
        this.target = target;
        this.id = UUID.fromString(object.get("uniqueId").getAsString());
        this.rankId = object.get("group").getAsString();
        this.rank = rank;
        this.issuedBy = object.get("addedBy").isJsonNull() ? null : UUID.fromString(object.get("addedBy").getAsString());
        this.issuedAt = object.get("addedAt").getAsLong();
        this.reason = object.get("addedReason").getAsString();
        this.duration = object.get("duration").getAsLong();

        if (object.has("removed") && object.get("removed").getAsBoolean()) {
            if (!object.get("removedBy").isJsonNull())
                setRemovedBy(UUID.fromString(object.get("removedBy").getAsString()));
            if (!object.get("removedAt").isJsonNull()) setRemovedAt(object.get("removedAt").getAsLong());
            if (!object.get("removedReason").isJsonNull()) setRemovalReason(object.get("removedReason").getAsString());
        }
    }

    public String getIssuedByName() {
        try {
            return MojangUtils.fetchName(issuedBy);
        } catch (Exception x) {
            return "Console";
        }
    }

    public String getIssuedByStyles() {
        try {
            return userRepository.findById(issuedBy).orElse(null).getRankColor();
        } catch (Exception x) {
            return "text-danger";
        }
    }

    public String getRemovedByName() {
        try {
            return MojangUtils.fetchName(removedBy);
        } catch (Exception x) {
            return "Console";
        }
    }

    public String getRemovedByStyles() {
        try {
            return userRepository.findById(removedBy).orElse(null).getRankColor();
        } catch (Exception x) {
            return "text-danger";
        }
    }

    public boolean isActive() {
        if (removedAt == null) {
            if (duration != null && duration != Long.MAX_VALUE) {
                if (System.currentTimeMillis() >= (issuedAt + duration)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Grant o) {
        return Boolean.compare(!this.isActive(), !o.isActive());
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("uniqueId", getId().toString());
        object.addProperty("group", getRankId());
        object.addProperty("addedBy", getIssuedBy() == null ? null : getIssuedBy().toString());
        object.addProperty("addedAt", getIssuedAt());
        object.addProperty("addedReason", getReason());
        object.addProperty("duration", getDuration());
        if (!isActive()) {
            object.addProperty("removedBy", getRemovedBy() == null ? null : getRemovedBy().toString());
            object.addProperty("removedAt", getRemovedAt());
            object.addProperty("removedReason", getRemovalReason());
            object.addProperty("removed", !isActive());
        }
        return object;
    }
}
