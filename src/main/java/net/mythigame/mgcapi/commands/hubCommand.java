package net.mythigame.mgcapi.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.mythigame.mgcapi.MGCAPI;

public class hubCommand extends Command {

    private final String hub = "hub";
    public hubCommand(){
        super("hub", "", "lobby", "leave");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ServerInfo targetServer = ProxyServer.getInstance().getServerInfo("hub");
        if(sender instanceof ProxiedPlayer player){
            if(args.length == 1){
                ProxiedPlayer target = MGCAPI.getInstance().getProxy().getPlayer(args[0]);
                if(target != null){
                    if(!target.getServer().getInfo().getName().equalsIgnoreCase("hub")){
                        target.connect(targetServer);
                    }else {
                        player.sendMessage(new TextComponent("§9[Zeus] Ce joueur est déjà sur le hub."));
                    }
                }
            }else{
                if(!player.getServer().getInfo().getName().equalsIgnoreCase("hub")){
                    player.connect(targetServer);
                }else{
                    player.sendMessage(new TextComponent("§9[Zeus] Vous êtes déjà sur le hub."));
                }
            }
        }
    }

    private void helpMessage(CommandSender sender){
        sender.sendMessage(new TextComponent("§9[Zeus] Usage : /hub"));
    }
}
