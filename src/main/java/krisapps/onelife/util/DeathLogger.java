package krisapps.onelife.util;

import krisapps.onelife.OneLife;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DeathLogger {

    OneLife main;
    public DeathLogger(OneLife main) {
        this.main = main;
    }

    public void logDeath(Player p, Location deathPosition, int livesLeft) {

        if (!main.pluginConfig.getBoolean("settings.enable-deathlogger")) { return; }

        main.appendToDeathLog(main.localizationUtility.getLocalizedPhrase("deathlogger.logmsg-dead")
                .replaceAll("%player%", p.getName())
                .replaceAll("%deathPosition%", "X: " + deathPosition.getBlockX() + " Y: " + deathPosition.getBlockY() + " Z: " + deathPosition.getBlockZ() + ", world: " + deathPosition.getWorld().getName())
                .replaceAll("%lives%", String.valueOf(livesLeft))
        );
    }

    public void logFinalDeath(Player p, Location deathPosition, LocalDateTime expiration) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/ss HH:mm:ss");

        if (!main.pluginConfig.getBoolean("settings.enable-deathlogger")) { return; }

        main.appendToDeathLog(main.localizationUtility.getLocalizedPhrase("deathlogger.logmsg")
                .replaceAll("%player%", p.getName())
                .replaceAll("%deathPosition%", "X: " + deathPosition.getBlockX() + " Y: " + deathPosition.getBlockY() + " Z: " + deathPosition.getBlockZ() + ", world: " + deathPosition.getWorld().getName())
                .replaceAll("%deathExpiration%", formatter.format(expiration))
        );
    }

}
