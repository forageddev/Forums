package gg.manny.forums.user.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PunishmentType {

    WARN("warned", "#fff"),
    KICK("kicked", "#E74C3C"),
    MUTE("muted", "#F8C471"),
    BAN("banned", "#B03A2E"),
    BLACKLIST("blacklisted", "#B03A2E");

    private final String action;
    private final String color;

}
