package krisapps.onelife.util;

import krisapps.onelife.OneLife;
import krisapps.onelife.events.LifeDepleteEvent;
import krisapps.onelife.types.DepletionContext;
import krisapps.onelife.types.LifeDepleteAction;
import krisapps.onelife.types.PlayerRecord;
import org.bukkit.BanList;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

            Date expirationDate = generateExpirationDate(Date.from(Instant.now()), getReplenishmentPeriod());
            main.pluginData.set("onelife.players." + p.getUniqueId() + ".lives", 0);
            main.pluginData.set("onelife.players." + p.getUniqueId() + ".deathTime", Date.from(Instant.now()));
            main.pluginData.set("onelife.players." + p.getUniqueId() + ".deathExpiration", expirationDate);
            main.pluginData.set("onelife.players." + p.getUniqueId() + ".deathPosition", p.getLocation());
            main.pluginData.set("onelife.players." + p.getUniqueId() + ".playerName", p.getName());
            main.saveData();

            main.deathLogger.logFinalDeath(p, p.getLocation(), expirationDate);
        } else {
            main.pluginData.set("onelife.players." + p.getUniqueId() + ".lives", main.pluginData.getInt("onelife.players." + p.getUniqueId() + ".lives") - 1);
            main.pluginData.set("onelife.players." + p.getUniqueId() + ".playerName", p.getName());
            main.saveData();

            main.deathLogger.logDeath(p, p.getLocation(), getLives(p));
        }
    }

    public int getLives(Player p){
        return main.pluginData.getInt("onelife.players." + p.getUniqueId() + ".lives");
    }

    public Location getDeathLocation (Player p) {
        return main.pluginData.getLocation("onelife.players." + p.getUniqueId() + ".deathPosition");
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

    public LifeDepleteAction getLifeDepleteAction() {
        if (main.pluginConfig.getString("settings.depletion-action").equals("ban")){
            return LifeDepleteAction.BAN_PLAYER;
        } else if (main.pluginConfig.getString("settings.depletion-action").equals("spectate")){
            return LifeDepleteAction.ALLOW_SPECTATE;
        } else {
            return null;
        }
    }

    public Date getFinalDeathTime(Player p){
        return main.pluginData.getObject("onelife.players." + p.getUniqueId() + ".deathExpiration", Date.class);
    }

    public Date generateExpirationDate(Date startingDate, int duration) {
        return new Date(startingDate.getTime() + TimeUnit.MINUTES.toMillis(duration));
    }

    public String generateDurationString(Date start, Date current) {
        Instant startInstant = start.toInstant();
        Instant endInstant = current.toInstant();

        Duration dur = Duration.between(startInstant, endInstant);

        if (dur.isNegative()) {
            return main.localizationUtility.getLocalizedPhrase("messages.timer-moment");
        }

        long hours = Math.abs(dur.toHours());
        long minutes = Math.abs(dur.minusHours(hours).toMinutes());
        long seconds = Math.abs(dur.minusHours(hours).minusMinutes(minutes).toSeconds());

        return String.format("%s:%s:%s", formatTimeUnit((int) hours), formatTimeUnit((int) minutes), formatTimeUnit((int) seconds));
    }

    public static String formatTimeUnit(int unit) {
        return unit <= 9
                ? "0" + unit
                : String.valueOf(unit);
    }

    public boolean isEnabled() {
        return main.pluginConfig.getBoolean("settings.enabled");
    }

    public boolean hasDied(Player p) {
        if (p == null) { return false; }
        return main.pluginData.contains("onelife.players." + p.getUniqueId() + ".deathTime");
    }

    public boolean hasDied(UUID playerUUID) {
        return main.pluginData.contains("onelife.players." + playerUUID + ".deathTime");
    }

    public PlayerRecord getPlayer(Player p) {
        if (p == null) { return null; }
        return new PlayerRecord(
                p.getUniqueId(),
                main.pluginData.getString("onelife.players." + p.getUniqueId() + ".playerName")
        );
    }

    public String getPlayerNameByUUID(UUID uuid) {
        return main.pluginData.getString("onelife.players." + uuid.toString() + ".playerName");
    }

    public Set<String> getPlayers(){
        if (main.pluginData.getConfigurationSection("onelife.players") == null) { return new HashSet<>(0); }
        return main.pluginData.getConfigurationSection("onelife.players").getKeys(false);
    }

    public void createOneLifeEntry(Player p) {
        if (p == null) { return; }
        main.pluginData.set("onelife.players." + p.getUniqueId() + ".lives", getDefaultLifeCount());
        main.pluginData.set("onelife.players." + p.getUniqueId() + ".deaths", 0);
        main.pluginData.set("onelife.players." + p.getUniqueId() + ".playerName", p.getName());
        main.saveData();
    }

    public void resetOneLifeEntry(Player p) {
        main.pluginData.set("onelife.players." + p.getUniqueId() + ".lives", getDefaultLifeCount());
        main.pluginData.set("onelife.players." + p.getUniqueId() + ".deathTime", null);
        main.saveData();
    }

    public void replenishLives(Player p) {
        main.pluginData.set("onelife.players." + p.getUniqueId() + ".lives", getDefaultLifeCount());
        main.pluginData.set("onelife.players." + p.getUniqueId() + ".deathTime", null);
        main.saveData();
    }

    public void replenishLives(UUID uuid) {
        main.pluginData.set("onelife.players." + uuid + ".lives", getDefaultLifeCount());
        main.pluginData.set("onelife.players." + uuid + ".deathTime", null);
        main.saveData();
    }

    public Date getDeathExpiration(Player p) {
        if (!hasDied(p)) { return null; }
        return main.pluginData.getObject("onelife.players." + p.getUniqueId() + ".deathExpiration", Date.class);
    }

    public Date getDeathExpiration(UUID playerUUID) {
        if (!hasDied(playerUUID)) { return null; }
        return main.pluginData.getObject("onelife.players." + playerUUID + ".deathExpiration", Date.class);
    }

    public void resetPlayer(Player onlinePlayer) {
        replenishLives(onlinePlayer);
        main.getServer().getBanList(BanList.Type.NAME).pardon(onlinePlayer.getName());
    }

    public void resetPlayer(UUID playerUUID) {
        replenishLives(playerUUID);
        main.getServer().getBanList(BanList.Type.NAME).pardon(
                getPlayerNameByUUID(playerUUID)
        );
    }

    public int getReplenishmentPeriod() {
        return main.pluginConfig.getInt("settings.life-replenishment-period");
    }
}
