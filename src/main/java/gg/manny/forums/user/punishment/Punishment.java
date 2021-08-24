package gg.manny.forums.user.punishment;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

import java.util.UUID;

@Getter @Setter
public class Punishment {

    @Id @NonNull private UUID id = UUID.randomUUID();
    private PunishmentType type;

    public String reason;

    private UUID issuedBy;
    private long issuedAt = System.currentTimeMillis();

    private Long duration, expiresAt;

    private String removalReason;
    private UUID removedBy;
    private Long removedAt;

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
