package net.mythigame.mgcapi.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.mythigame.commons.Account;

public class ProxyLeaveListener implements Listener {

    @EventHandler
    public void onPlayerLeaveListener(PlayerDisconnectEvent event){
        ProxiedPlayer player = event.getPlayer();
        Account account = new Account().getAccount(player.getUniqueId());
        if(account != null){
            account.setConnected(false);
            account.update();
        }
    }

}
