package dev.foraged.forums.user.punishment

enum class PunishmentType(val action: String, val color: String)
{
    WARN("warned", "#fff"),
    MUTE("muted", "#F8C471"),
    KICK("kicked", "#E74C3C"),
    BAN("banned", "#B03A2E"),
    BLACKLIST("blacklisted", "#B03A2E");
}