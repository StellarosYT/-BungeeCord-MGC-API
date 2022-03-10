package net.mythigame.commons.Storage.Redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisAccess {
    public static RedisAccess INSTANCE;
    private final RedissonClient redissonClient;

    public RedisAccess(RedisCredentials credentials) {
        INSTANCE = this;
        this.redissonClient = initRedisson(credentials);
    }

    public static void init(){
        new RedisAccess(new RedisCredentials("localhost", "KoPxS6PW?#?JzYh70dUI", 6379));
    }

    public static void close(){
        RedisAccess.INSTANCE.getRedissonClient().shutdown();
    }

    public RedissonClient initRedisson(RedisCredentials credentials){
        final Config config = new Config();

        config.setCodec(new JsonJacksonCodec());
        config.setThreads(2 * 2); // nombre de coeur x 2
        config.setNettyThreads(2 * 2); // pareil qu'au dessus
        config.useSingleServer()
                .setAddress(credentials.toRedisURI())
                .setPassword(credentials.getPassword())
                .setDatabase(1)
                .setClientName(credentials.getClientName());

        return Redisson.create(config);
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
