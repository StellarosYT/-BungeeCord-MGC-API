package net.mythigame.commons;

import java.util.Arrays;

public enum RankUnit {

    JOUEUR("Joueur", 0, "§7", "§7"),
    VIP("VIP", 10, "§e[VIP] ", "§e[VIP] "),
    MODERATEUR("Modérateur", 50, "§6[Modérateur] ", "§6[MOD] "),
    DEVELOPPEUR("Développeur", 90, "§9[Développeur] ", "§9[DEV] "),
    ADMINISTRATEUR("Administrateur", 100, "§c[Administrateur] ", "§c[ADMIN] ");

    private final String name;
    private final int power;
    private final String prefix;
    private final String tabPrefix;

    RankUnit(String name, int power, String prefix, String tabPrefix){
        this.name = name;
        this.power = power;
        this.prefix = prefix;
        this.tabPrefix = tabPrefix;
    }

    public static RankUnit getByName(String name){
        return Arrays.stream(values()).filter(r -> r.getName().equalsIgnoreCase(name)).findAny().orElse(RankUnit.JOUEUR);
    }

    public static RankUnit getByPower(int power){
        return Arrays.stream(values()).filter(r -> r.getPower() == power).findAny().orElse(RankUnit.JOUEUR);
    }

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTabPrefix(){
        return tabPrefix;
    }
}