package net.mythigame.commons;

import java.util.List;
import java.util.UUID;

public class Account implements Cloneable{
    private UUID uuid;
    private String username;
    private String nickname;
    private RankUnit grade;
    private long grade_end;
    private int coins;
    private int level;
    private int xp;
    private String guild;
    private List<AccountCase> cases;
    private boolean isBanned;
    private String ban_id;
    private boolean isMuted;
    private String mute_id;
    private boolean isFreeze;
    private boolean isVanished;
    private boolean isModeration;
    private boolean isNicked;
    private boolean isConnected;
    private String server;

    public Account(){}

    public Account(UUID uuid, String username, String nickname, RankUnit grade, long grade_end, int coins, int level, int xp, String guild, List<AccountCase> cases, boolean isBanned, String ban_id, boolean isMuted, String mute_id, boolean isFreeze, boolean isVanished, boolean isModeration, boolean isNicked, boolean isConnected, String server) {
        this.uuid = uuid;
        this.username = username;
        this.nickname = nickname;
        this.grade = grade;
        this.grade_end = grade_end;
        this.coins = coins;
        this.level = level;
        this.xp = xp;
        this.guild = guild;
        this.cases = cases;
        this.isBanned = isBanned;
        this.ban_id = ban_id;
        this.isMuted = isMuted;
        this.mute_id = mute_id;
        this.isFreeze = isFreeze;
        this.isVanished = isVanished;
        this.isModeration = isModeration;
        this.isNicked = isNicked;
        this.isConnected = isConnected;
        this.server = server;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public RankUnit getGrade() {
        return grade;
    }

    public void setGrade(RankUnit grade) {
        this.grade = grade;
    }

    public long getGrade_end() {
        return grade_end;
    }

    public void setGrade_end(long grade_end) {
        this.grade_end = grade_end;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getGuild() {
        return guild;
    }

    public void setGuild(String guild) {
        this.guild = guild;
    }

    public List<AccountCase> getCases() {
        return cases;
    }

    public void setCases(List<AccountCase> cases) {
        this.cases = cases;
    }

    public AccountCase getCaseById(String id) {
        AccountCase accountCase = null;
        int i;
        for (i = 0; i < getCases().size(); i++){
            if(getCases().get(i).getId().equalsIgnoreCase(id)){
                accountCase = getCases().get(i);
            }
        }
        return accountCase;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getBan_id() {
        return ban_id;
    }

    public void setBan_id(String ban_id) {
        this.ban_id = ban_id;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public String getMute_id() {
        return mute_id;
    }

    public void setMute_id(String mute_id) {
        this.mute_id = mute_id;
    }

    public boolean isFreeze() {
        return isFreeze;
    }

    public void setFreeze(boolean freeze) {
        isFreeze = freeze;
    }

    public boolean isVanished() {
        return isVanished;
    }

    public void setVanished(boolean vanished) {
        isVanished = vanished;
    }

    public boolean isModeration() {
        return isModeration;
    }

    public void setModeration(boolean moderation) {
        isModeration = moderation;
    }

    public boolean isNicked() {
        return isNicked;
    }

    public void setNicked(boolean nicked) {
        isNicked = nicked;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(!(o instanceof Account)){
            return false;
        } else {
            return ((Account)o).getUuid() == this.uuid;
        }
    }

    public Account clone(){
        try {
            return (Account) super.clone();
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }

        return null;
    }

    public void update(){
        new AccountProvider(uuid).updateRedisAccount(this);
    }

    public void removeFromRedis(){
        new AccountProvider(uuid).removeFromRedis();
    }

    public void sendToMySQL(){
        new AccountProvider(uuid).updateMySQLAccount(this);
    }

    public Account getAccount(UUID uuid){
        return new AccountProvider(uuid).getAccount();
    }
}
