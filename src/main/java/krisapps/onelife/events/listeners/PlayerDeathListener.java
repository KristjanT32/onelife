package krisapps.onelife.events.listeners;

import krisapps.onelife.OneLife;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    OneLife main;
    public PlayerDeathListener(OneLife main){
        this.main = main;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent deathEvent){
        if (!main.dataUtility.oneLifeEnabled()) { return; }
        main.dataUtility.depleteLife(deathEvent.getEntity(), 1, false);

        if (main.dataUtility.getLives(deathEvent.getEntity()) <= 0) {
            deathEvent.setDeathMessage(ChatColor.translateAlternateColorCodes('&', main.localizationUtility.getLocalizedPhrase("messages.death-message")));
        } else {
            main.messageUtility.sendMessage(deathEvent.getEntity(), main.localizationUtility.getLocalizedPhrase("messages.life-depleted")
                    .replaceAll("%player%", deathEvent.getEntity().getName())
                    .replaceAll("%livesLeft%", String.valueOf(main.dataUtility.getLives(deathEvent.getEntity())))
            );
        }
    }

}
