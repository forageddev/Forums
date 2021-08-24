package gg.manny.forums.user.grant;

import com.google.gson.JsonObject;
import gg.manny.forums.rank.Rank;
import gg.manny.forums.rank.RankRepository;
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

    @NonNull private UUID id;

    @Transient private String rankId; // Used for POST data to convert id into rank (limitations of Springboot)
    @DBRef @NonNull private Rank rank;

    public String reason;

    private UUID issuedBy;
    private long issuedAt = System.currentTimeMillis();

    private Long duration, expiresAt;

    private String removalReason;
    private UUID removedBy;
    private Long removedAt;

    public Grant(JsonObject object, Rank rank) {
        this.id = UUID.fromString(object.get("uniqueId").getAsString());
        this.rank = rank;
        this.issuedBy = object.get("addedBy").isJsonNull() ? null : UUID.fromString(object.get("addedBy").getAsString());
        this.issuedAt = object.get("addedAt").getAsLong();
        this.reason = object.get("addedReason").getAsString();
        this.duration = object.get("duration").getAsLong();

        if (!isActive()) {
            if (!object.get("removedBy").isJsonNull())
                setRemovedBy(UUID.fromString(object.get("removedBy").getAsString()));
            if (!object.get("removedAt").isJsonNull()) setRemovedAt(object.get("removedAt").getAsLong());
            if (!object.get("removedReason").isJsonNull()) setRemovalReason(object.get("removedReason").getAsString());
        }
    }

    public boolean isActive() {
        if (removedAt == null) {
            if (expiresAt != null) {
                if (System.currentTimeMillis() >= expiresAt) {
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
        object.addProperty("group", getRank().getId());
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
