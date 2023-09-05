package krisapps.onelife.events.listeners;

import krisapps.onelife.OneLife;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.permissions.ServerOperator;

import java.util.stream.Collectors;

public class GameModeChangeListener implements Listener {

    OneLife main;
    public GameModeChangeListener(OneLife main) {
        this.main = main;
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event){
        if (!main.dataUtility.isEnabled()) { return; }
        if (event.getPlayer().isOp()) { return; }
        if (event.getPlayer().hasPermission("onelife.ignore")) { return; }

        if (main.dataUtility.hasDied(event.getPlayer())){
            event.setCancelled(true);
            main.messageUtility.sendMessage(event.getPlayer(), main.localizationUtility.getLocalizedPhrase("messages.gmchange-fail"));
            if (main.pluginConfig.getBoolean("settings.notify-dirty-cheater")) {
                for (Player p: Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).collect(Collectors.toList())){
                    main.messageUtility.sendMessage(p, main.localizationUtility.getLocalizedPhrase("gmchange-op")
                            .replaceAll("%player%", event.getPlayer().getName())
                    );
                }
            }

        }
    }


}
