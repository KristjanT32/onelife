package krisapps.onelife.events.listeners;

import krisapps.onelife.OneLife;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener {

    OneLife main;
    public PlayerLoginListener(OneLife main) {
        this.main = main;
    }


    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (!main.dataUtility.hasOneLifeRecord(event.getPlayer())) {
            main.getLogger().info("Created new OneLife record for " + event.getPlayer().getName());
            main.dataUtility.createOneLifeEntry(event.getPlayer());
        }
    }

}
