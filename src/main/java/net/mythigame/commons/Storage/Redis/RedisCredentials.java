package net.mythigame.commons.Storage.Redis;

public class RedisCredentials {
    private final String ip;
    private final String password;
    private final int port;
    private final String clientName;

    public RedisCredentials(String ip, String password, int port) {
        this.ip = ip;
        this.password = password;
        this.port = port;
        this.clientName = "mgcapi_bungeecord";
    }

    public RedisCredentials(String ip, String password, int port, String clientName) {
        this.ip = ip;
        this.password = password;
        this.port = port;
        this.clientName = clientName;
    }

    public String getIp() {
        return ip;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getClientName() {
        return clientName;
    }

    public String toRedisURI(){
        return "redis://"+ ip +":" + port;
    }
}
