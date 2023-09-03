package krisapps.onelife.util;

import krisapps.onelife.OneLife;
import krisapps.onelife.events.LifeDepleteEvent;
import krisapps.onelife.types.DepletionContext;
import org.bukkit.entity.Player;

public class DataUtility {

    OneLife main;
    public DataUtility(OneLife main) {
        this.main = main;
    }

    public String getCurrentLanguage(){
        return main.pluginConfig.getString("settings.language");
    }

    public void setCurrentLanguage(String lan){
        main.pluginConfig.set("settings.language", lan);
        main.saveConfig();
    }

    public void setLives(Player p, int lives){
        main.pluginData.set("onelife.players." + p.getUniqueId() + ".lives", lives);
        main.saveData();
    }

    public void depleteLife(Player p, int numberOfLivesToDeplete, boolean forced){
        if (main.pluginData.getInt("onelife.players." + p.getUniqueId() + ".lives") - numberOfLivesToDeplete <= 0){
            main.pluginData.set("onelife.players." + p.getUniqueId() + ".lives", 0);
            main.saveData();
        } else {
            main.pluginData.set("onelife.players." + p.getUniqueId() + ".lives", main.pluginData.getInt("onelife.players." + p.getUniqueId() + ".lives") - 1);
            main.saveData();
        }
        main.getServer().getPluginManager().callEvent(new LifeDepleteEvent(p,
                forced ? DepletionContext.FORCED : DepletionContext.NATURAL,
                getLives(p)

        ));
    }

    public int getLives(Player p){
        return main.pluginData.getInt("onelife.players." + p.getUniqueId() + ".lives");
    }

    public int getDefaultLifeCount(){
        return main.pluginConfig.getInt("settings.default-life-number");
    }

    public int getDeaths(Player p){
        return main.pluginData.getInt("onelife.players." + p.getUniqueId() + ".deaths");
    }

    public void registerDeath(Player p){
        int currentDeaths = getDeaths(p);
        main.pluginData.set("onelife.players." + p.getUniqueId() + ".deaths", currentDeaths + 1);
        main.saveData();
    }

    public boolean hasOneLifeRecord(Player p){
        return main.pluginData.getConfigurationSection("onelife.players." + p.getUniqueId()) != null;
    }

    public boolean oneLifeEnabled(){
        return main.pluginConfig.getBoolean("settings.enabled");
    }

    public void setEnabled(boolean enabled){
        main.pluginConfig.set("settings.enabled", enabled);
        main.saveConfig();
    }
}
