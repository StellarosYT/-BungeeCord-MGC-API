package net.mythigame.mgcapi;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.mythigame.commons.Account;
import net.mythigame.commons.Storage.MySQL.MySQLManager;
import net.mythigame.commons.Storage.Redis.RedisAccess;
import net.mythigame.mgcapi.commands.hubCommand;
import net.mythigame.mgcapi.listeners.ProxyJoinListener;
import net.mythigame.mgcapi.listeners.ProxyLeaveListener;
import net.mythigame.mgcapi.listeners.ProxyPingListener;
import net.mythigame.mgcapi.listeners.ServerSwitchListener;
import org.redisson.api.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static net.mythigame.commons.AccountProvider.createAccountsCasesTables;
import static net.mythigame.commons.AccountProvider.createAccountsTables;


public final class MGCAPI extends Plugin{

    private static MGCAPI INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        registerListeners();
        registerCommands();

        MySQLManager.initAllConnection();
        RedisAccess.init();

        createAccountsTables();
        createAccountsCasesTables();

        updateSQLAccounts();

    }

    @Override
    public void onDisable() {
        shutdownSave();
        resetRedisCache();
        RedisAccess.close();
        MySQLManager.closeAllConnection();
    }

    public static MGCAPI getInstance(){
        return INSTANCE;
    }

    private void registerListeners(){
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerListener(this, new ProxyPingListener());
        pluginManager.registerListener(this, new ProxyJoinListener());
        pluginManager.registerListener(this, new ProxyLeaveListener());
        pluginManager.registerListener(this, new ServerSwitchListener());
    }

    private void registerCommands(){
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerCommand(this, new hubCommand());
    }

    public static void resetRedisCache(){
        final RedisAccess redisAccess = RedisAccess.INSTANCE;
        final RedissonClient redissonClient = redisAccess.getRedissonClient();
        redissonClient.getKeys().flushdb();
    }

    private void updateSQLAccounts(){
        this.getProxy().getScheduler().schedule(this, () -> {
            final RedisAccess redisAccess = RedisAccess.INSTANCE;
            final RedissonClient redissonClient = redisAccess.getRedissonClient();
            Set<Account> accounts = new HashSet<>();
            Iterable<String> keys = redissonClient.getKeys().getKeysByPattern("account:*");
            for(String key : keys){
                accounts.add((Account) redissonClient.getBucket(key).get());
            }
            accounts.forEach(account -> {
                if(this.getProxy().getPlayer(account.getUuid()) == null){
                    account.setConnected(false);
                    account.update();
                }
                account.sendToMySQL();
                if(this.getProxy().getPlayer(account.getUuid()) == null){
                    account.removeFromRedis();
                }
            });
        }, 1L, 15, TimeUnit.MINUTES);
    }

    private void shutdownSave(){
        final RedisAccess redisAccess = RedisAccess.INSTANCE;
        final RedissonClient redissonClient = redisAccess.getRedissonClient();
        Set<Account> accounts = new HashSet<>();
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern("account:*");
        for(String key : keys){
            accounts.add((Account) redissonClient.getBucket(key).get());
        }
        accounts.forEach(account -> {
            account.setConnected(false);
            account.update();
            account.sendToMySQL();
            account.removeFromRedis();
        });
    }
}
