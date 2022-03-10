package net.mythigame.mgcapi.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.mythigame.commons.Account;

public class ServerSwitchListener implements Listener {

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event){
        ProxiedPlayer player = event.getPlayer();
        final Account account = new Account().getAccount(player.getUniqueId());
        if(account != null){
            account.setServer(event.getServer().getInfo().getName());
            account.update();
        }
    }

}
