package net.mythigame.commons.Utils;

import org.shanerx.mojang.Mojang;

import java.util.UUID;
import java.util.regex.Pattern;

public class MojangAPI {

    private static final Mojang mojangAPI = new Mojang().connect();

    public static Mojang getMojangAPI(){
        return mojangAPI;
    }

    private static final Pattern STRIPPED_UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    public static UUID getUUIDFromUsername(String username){
        return UUID.fromString(STRIPPED_UUID_PATTERN.matcher(MojangAPI.getMojangAPI().getUUIDOfUsername(username)).replaceAll("$1-$2-$3-$4-$5"));
    }

}
