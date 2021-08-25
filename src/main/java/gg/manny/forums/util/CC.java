package gg.manny.forums.util;

import java.util.Locale;

public class CC {

    public static String convert(String string) {
        switch (string.toLowerCase(Locale.ROOT)) {
            case "owner": { return "darkred"; }
            case "admin": { return "red"; }
            case "default": { return "green"; }

            default: return "#000";
        }
    }
}
