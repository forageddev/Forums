package gg.manny.forums.util;

import java.util.Locale;

public class CC {

    public static String convert(String string) {
        switch (string.toLowerCase(Locale.ROOT)) {
            case "owner": { return "#AA0000"; }
            case "developer": { return "#5555FF"; }
            case "admin": { return "#FF5555"; }
            case "mod":
            case "trial-mod": { return "#00AAAA"; }
            case "default": { return "green"; }

            default: return "#000";
        }
    }
}
