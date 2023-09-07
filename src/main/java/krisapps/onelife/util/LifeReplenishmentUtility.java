package krisapps.onelife.util;

import krisapps.onelife.OneLife;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class LifeReplenishmentUtility {

    private int REPLENISH_TASK = -1;

    OneLife main;
    public LifeReplenishmentUtility(OneLife main) {
        this.main = main;
    }

    public void start() {
        if (REPLENISH_TASK != -1) {
            stop();
        }

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {

                main.appendToLog("[System]: Checking player death expiration dates");

                // Replenish all eligible players' lives.
                for (String playerUUID: main.dataUtility.getPlayers()) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
                    Player onlinePlayer = Bukkit.getPlayer(UUID.fromString(playerUUID));

                    if (onlinePlayer == null && !offlinePlayer.hasPlayedBefore()) { continue; }
                    if (!main.dataUtility.hasDied(onlinePlayer) && !main.dataUtility.hasDied(offlinePlayer.getUniqueId())) { continue; }

                    switch (main.dataUtility.getLifeDepleteAction()) {
                        case BAN_PLAYER:

                            if (main.dataUtility.getDeathExpiration(onlinePlayer) != null) {
                                // If the player is online
                                if (Date.from(Instant.now()).after(main.dataUtility.getDeathExpiration(onlinePlayer))) {
                                    main.appendToLog("Replenishing lives for player " + onlinePlayer.getName());
                                    main.dataUtility.resetPlayer(onlinePlayer);
                                    main.messageUtility.sendActionbarMessage(onlinePlayer, main.localizationUtility.getLocalizedPhrase("messages.spectator-replenish"));
                                }

                            } else {
                                if (main.dataUtility.getDeathExpiration(offlinePlayer.getUniqueId()) != null) {
                                    // If the player is offline
                                    if (Date.from(Instant.now()).after(main.dataUtility.getDeathExpiration(offlinePlayer.getUniqueId()))) {
                                        main.appendToLog("Replenishing lives for player " + main.dataUtility.getPlayerNameByUUID(UUID.fromString(playerUUID)));
                                        main.dataUtility.resetPlayer(UUID.fromString(playerUUID));
                                    }
                                }
                            }
                            break;

                        case ALLOW_SPECTATE:
                            if (onlinePlayer == null) { continue; }
                            if (Date.from(Instant.now()).after(main.dataUtility.getDeathExpiration(onlinePlayer))){
                                main.dataUtility.replenishLives(onlinePlayer);
                                main.getServer().getScheduler().runTaskLater(main, () -> onlinePlayer.setGameMode(GameMode.SURVIVAL), 0L);

                                Location dLoc = main.dataUtility.getDeathLocation(onlinePlayer);
                                TextComponent button = new TextComponent(ChatColor.translateAlternateColorCodes('&', main.localizationUtility.getLocalizedPhrase("messages.teleport-button")));

                                button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleport " + onlinePlayer.getName() + " " + dLoc.getBlockX() + " " + dLoc.getBlockY() + " " + dLoc.getBlockZ()));
                                onlinePlayer.spigot().sendMessage(
                                        new TextComponent(ChatColor.translateAlternateColorCodes('&', main.localizationUtility.getLocalizedPhrase("messages.teleport"))),
                                        button

                                );
                                main.appendToLog("[System]: Replenished lives for " + onlinePlayer.getName());
                            } else {
                                main.appendToLog("[System]: Skipping " + onlinePlayer.getName() + " - not yet.");
                            }
                            break;
                    }
                }


            }
        }, 0, 20L);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(REPLENISH_TASK);
    }


}
