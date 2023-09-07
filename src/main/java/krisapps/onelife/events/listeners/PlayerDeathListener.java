package krisapps.onelife.events.listeners;

import krisapps.onelife.OneLife;
import krisapps.onelife.events.LifeDepleteEvent;
import krisapps.onelife.types.DepletionContext;
import krisapps.onelife.types.LifeDepleteAction;
import krisapps.onelife.util.DataUtility;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class PlayerDeathListener implements Listener {

    OneLife main;
    public PlayerDeathListener(OneLife main){
        this.main = main;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent deathEvent){
        if (!main.dataUtility.oneLifeEnabled()) { return; }
        if (deathEvent.getEntity().hasPermission("onelife.ignore")) { return; }

        main.dataUtility.depleteLife(deathEvent.getEntity(), 1, false);
        main.dataUtility.registerDeath(deathEvent.getEntity());

        int livesLeft = main.dataUtility.getLives(deathEvent.getEntity());
        if (livesLeft > 0){
            deathEvent.setDeathMessage(ChatColor.translateAlternateColorCodes('&', deathEvent.getDeathMessage() + main.localizationUtility.getLocalizedPhrase("messages.ondeath-lifecounter")
                    .replaceAll("%lives%", String.valueOf(livesLeft))
            ));
        } else {
            deathEvent.setDeathMessage(ChatColor.translateAlternateColorCodes('&', deathEvent.getDeathMessage() + main.localizationUtility.getLocalizedPhrase("messages.ondeath-lifecounter-zero")));
        }
        main.getServer().getPluginManager().callEvent(new LifeDepleteEvent(deathEvent.getEntity(), DepletionContext.NATURAL, livesLeft));
    }

    @EventHandler
    public void onLifeDepleted(LifeDepleteEvent event){
        // Has the player depleted all their lives?
        if (main.dataUtility.getLives(event.getPlayer()) <= 0) {

            // Notify others
            for (Player p: Bukkit.getOnlinePlayers()){
                if (p.isDead() || p.getUniqueId() == event.getPlayer().getUniqueId()) { continue; }
                main.messageUtility.sendMessage(p, main.localizationUtility.getLocalizedPhrase("messages.player-died")
                        .replaceAll("%player%", p.getName())
                );
            }

            // Perform on-death action
            if (main.dataUtility.getLifeDepleteAction().equals(LifeDepleteAction.BAN_PLAYER)){
                // Ban the player for the specified duration.

                // Generating an expiration date.
                Date expirationDate = main.dataUtility.generateExpirationDate(
                        Date.from(Instant.now()),
                        main.dataUtility.getReplenishmentPeriod()
                );

                // Adding a ban entry
                main.getServer().getBanList(BanList.Type.NAME).addBan(
                        event.getPlayer().getUniqueId().toString(),
                        ChatColor.translateAlternateColorCodes('&', main.localizationUtility.getLocalizedPhrase("messages.ban-message")
                                .replaceAll("%date%", expirationDate.toString())
                        ),
                        expirationDate,
                        "Server"
                );
                Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                    @Override
                    public void run() {
                        event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', main.localizationUtility.getLocalizedPhrase("messages.on-ban")));
                    }
                }, 20L);
            } else if (main.dataUtility.getLifeDepleteAction().equals(LifeDepleteAction.ALLOW_SPECTATE)){
                // Forcing the player into spectator mode.
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
                main.messageUtility.sendMessage(event.getPlayer(), main.localizationUtility.getLocalizedPhrase("messages.spectate-message"));
            }
        } else {
            // If the player still has lives left

            main.messageUtility.sendMessage(event.getPlayer(), main.localizationUtility.getLocalizedPhrase("messages.death-player")
                    .replaceAll("%lives%", String.valueOf(main.dataUtility.getLives(event.getPlayer())))
            );

            for (Player p: Bukkit.getOnlinePlayers()){
                if (p.isDead() || p.getUniqueId() == event.getPlayer().getUniqueId()) { continue; }
                main.messageUtility.sendMessage(event.getPlayer(), main.localizationUtility.getLocalizedPhrase("messages.life-depleted")
                        .replaceAll("%player%", event.getPlayer().getName())
                        .replaceAll("%livesLeft%", String.valueOf(main.dataUtility.getLives(event.getPlayer())))
                );
            }
        }
    }

}
