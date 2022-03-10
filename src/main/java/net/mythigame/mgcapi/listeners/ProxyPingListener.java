package net.mythigame.mgcapi.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyPingListener implements Listener {

    @EventHandler
    public void onProxyPing(ProxyPingEvent event){
        final ServerPing serverPing = event.getResponse();

        serverPing.setDescriptionComponent(new TextComponent(ChatColor.RED + "MGC | Triolet Minecraft Server"));
    }
}
