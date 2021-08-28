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

    private Long duration;

    private String removalReason;
    private UUID removedBy;
    private Long removedAt;

    public Punishment(JsonObject object) {
        this.id = UUID.fromString(object.get("uniqueId").getAsString());
        this.type = PunishmentType.valueOf(object.get("type").getAsString());
        this.issuedBy = object.get("issuedBy").isJsonNull() ? null : UUID.fromString(object.get("issuedBy").getAsString());
        this.issuedAt = object.get("issuedAt").getAsLong();
        this.reason = object.get("reason").getAsString();
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
            if (duration != null && duration != Long.MAX_VALUE) {
                if (System.currentTimeMillis() >= (issuedAt + duration)) {
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
        object.addProperty("issuedBy", getIssuedBy() == null ? null : getIssuedBy().toString());
        object.addProperty("issuedAt", getIssuedAt());
        object.addProperty("reason", getReason());
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
