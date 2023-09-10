package krisapps.onelife.util;

import krisapps.onelife.OneLife;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

public class InfoUpdater {

    OneLife main;
    public InfoUpdater(OneLife main){
        this.main = main;
    }

    private int MODIFIER_TASK = -1;

    public void start(){
        main.appendToLog("[Info Updater]: Starting a new updater task");
        stop();
        MODIFIER_TASK = Bukkit.getScheduler().scheduleAsyncRepeatingTask(main, () -> modifierCycle(), 0, 20L);

        if (!main.dataUtility.isEnabled()) {
            stop();
        }
    }

    public void stop(){
        if (MODIFIER_TASK != -1) {
            main.appendToLog("[Info Updater]: Stopping existing updater task no." + MODIFIER_TASK);
            Bukkit.getScheduler().cancelTask(MODIFIER_TASK);
        }
    }

    private void modifierCycle(){

        // Show an actionbar message with the time their lives will replenish to all players spectating.
        for (Player p: Bukkit.getOnlinePlayers().stream().filter(player -> main.dataUtility.hasDied(player)).collect(Collectors.toSet())){
            if (main.dataUtility.getFinalDeathTime(p) == null) {
                main.appendToLog("[Info Updater]: Skipping " + p.getName() + ", invalid death time.");
                continue;
            }

            main.messageUtility.sendActionbarMessage(p, main.localizationUtility.getLocalizedPhrase("messages.actionbar-replenish")
                    .replaceAll("%time%", main.dataUtility.generateDurationString(Date.from(Instant.now()), main.dataUtility.getFinalDeathTime(p)))
            );
        }

        for (Player p: Bukkit.getOnlinePlayers().stream().filter(player -> !main.dataUtility.hasDied(player)).collect(Collectors.toSet())) {
            main.messageUtility.sendActionbarMessage(p, main.localizationUtility.getLocalizedPhrase("messages.lifecounter")
                    .replaceAll("%health%", String.valueOf(Math.ceil(p.getHealth())))
                    .replaceAll("%max%", String.valueOf(Math.ceil(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())))
                    .replaceAll("%lives%", String.valueOf(main.dataUtility.getLives(p)))
                    .replaceAll("%deaths%", String.valueOf(main.dataUtility.getDeaths(p)))
            );
        }

    }

}
