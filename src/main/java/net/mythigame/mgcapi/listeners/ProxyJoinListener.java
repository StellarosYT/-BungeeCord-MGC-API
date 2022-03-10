package net.mythigame.mgcapi.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.mythigame.commons.Account;
import net.mythigame.commons.AccountCase;
import net.mythigame.commons.Exceptions.AccountNotFoundException;
import net.mythigame.commons.Utils.MojangAPI;
import net.mythigame.mgcapi.MGCAPI;
import net.mythigame.commons.AccountProvider;
import org.shanerx.mojang.Mojang;

import static net.mythigame.commons.Utils.TimeUnit.checkDuration;
import static net.mythigame.commons.Utils.TimeUnit.getTimeLeft;

public class ProxyJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinProxy(PostLoginEvent event){
        final ProxiedPlayer player = event.getPlayer();

        MGCAPI.getInstance().getProxy().getScheduler().runAsync(MGCAPI.getInstance(), () -> {
            try {
                final Account account = new AccountProvider(player.getUniqueId()).getAccountOnLogin();
                if(account != null){
                    account.setUsername(player.getName());
                    account.setConnected(true);
                    account.update();
                    if(account.isBanned()){
                        final AccountCase accountCase = account.getCaseById(account.getBan_id());
                        if(accountCase != null){
                            checkDuration(account, accountCase);
                            if(account.isBanned()){
                                player.disconnect(new TextComponent("§cVous avez été banni !\n " +
                                        "\n " +
                                        "§6Raison : §f" + accountCase.getReason() + "\n " +
                                        "\n " +
                                        "§aTemps restant : §f" + getTimeLeft(accountCase)
                                ));
                            }
                        }
                    }
                }else{
                    player.disconnect(new TextComponent("§9[Zeus] Une erreur est survenue. Veuillez vous reconnecter."));
                }
            } catch (AccountNotFoundException e) {
                e.printStackTrace();
                player.disconnect(new TextComponent("§9[Zeus] Une erreur est survenue. Veuillez vous reconnecter."));
            }
        });

        ServerInfo targetServer = ProxyServer.getInstance().getServerInfo("hub");
        player.setReconnectServer(targetServer);

        player.setTabHeader(new TextComponent("§3§k|||§r§3 Bienvenue sur MythiGame Community §3§k|||§r \n "),
                new TextComponent("\n Site : https://www.mythigame.net/\n" +
                        "Discord : https://discord.gg/Txe9z6T"));
    }
    public void onPlayerLogin(LoginEvent event){
        if (MojangAPI.getMojangAPI().getStatus(Mojang.ServiceType.AUTHSERVER_MOJANG_COM) != Mojang.ServiceStatus.GREEN) {
            System.err.println("The Auth Server is not available right now.");
            event.setCancelled(true);
            event.setCancelReason(new TextComponent("§cLes serveurs de Mojang sont hors-ligne. Veuillez réessayer ultérieurement."));
        }
    }
}
