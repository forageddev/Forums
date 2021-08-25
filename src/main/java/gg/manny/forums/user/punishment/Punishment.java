package gg.manny.forums.user.punishment;

import com.google.gson.JsonObject;
import gg.manny.forums.rank.Rank;
import gg.manny.forums.rank.RankRepository;
import gg.manny.forums.user.UserRepository;
import gg.manny.forums.util.MojangUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor
public class Punishment {

    @Autowired private RankRepository repository;
    @Autowired private UserRepository userRepository;

    @Id @NonNull private UUID id = UUID.randomUUID();
    private PunishmentType type;

    public String reason;

    private UUID issuedBy;
    private long issuedAt = System.currentTimeMillis();

    private Long duration, expiresAt;

    private String removalReason;
    private UUID removedBy;
    private Long removedAt;

    public Punishment(JsonObject object) {
        this.id = UUID.fromString(object.get("uniqueId").getAsString());
        this.type = PunishmentType.valueOf(object.get("type").getAsString());
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
            if (expiresAt != null) {
                if (System.currentTimeMillis() >= expiresAt) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("uniqueId", getId().toString());
        object.addProperty("type", getType().name());
        object.addProperty("addedBy", getIssuedBy() == null ? null : getIssuedBy().toString());
        object.addProperty("addedAt", getIssuedAt());
        object.addProperty("addedReason", getReason());
        object.addProperty("duration", getDuration());
        object.addProperty("removedBy", getRemovedBy() == null ? null : getRemovedBy().toString());
        object.addProperty("removedAt", getRemovedAt());
        object.addProperty("removedReason", getRemovalReason());
        object.addProperty("removed", !isActive());
        return object;
    }
}
