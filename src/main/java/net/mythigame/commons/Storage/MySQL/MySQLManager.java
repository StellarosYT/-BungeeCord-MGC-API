package net.mythigame.commons.Storage.MySQL;

public enum MySQLManager {

    MGC(new MySQLCredentials("localhost", "minecraft", "KoPxS6PW?#?JzYh70dUI", "minecraft", 3306));

    private final MySQLAccess mySQLAccess;

    MySQLManager(MySQLCredentials credentials){
        this.mySQLAccess = new MySQLAccess(credentials);
    }

    public MySQLAccess getMySQLAccess() {
        return mySQLAccess;
    }

    public static void initAllConnection(){
        for(MySQLManager mySQLManager : values()){
            mySQLManager.mySQLAccess.initPool();
        }
    }

    public static void closeAllConnection(){
        for(MySQLManager mySQLManager : values()){
            mySQLManager.mySQLAccess.closePool();
        }
    }
}
