package krisapps.onelife;

import krisapps.onelife.commands.LifeConfig;
import krisapps.onelife.commands.LifeStats;
import krisapps.onelife.commands.tabcompleter.OneLifeTab;
import krisapps.onelife.events.listeners.GameModeChangeListener;
import krisapps.onelife.events.listeners.PlayerDeathListener;
import krisapps.onelife.events.listeners.PlayerLoginListener;
import krisapps.onelife.util.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

public final class OneLife extends JavaPlugin {

    public DataUtility dataUtility = new DataUtility(this);
    public MessageUtility messageUtility = new MessageUtility(this);
    public LocalizationUtility localizationUtility = new LocalizationUtility(this);
    public InfoUpdater infoUpdater = new InfoUpdater(this);
    public LifeReplenishmentUtility replenishmentUtility = new LifeReplenishmentUtility(this);
    public DeathLogger deathLogger = new DeathLogger(this);

    public FileConfiguration pluginConfig;
    public File configFile = new File(getDataFolder(), "config.yml");

    public FileConfiguration pluginData;
    public File dataFile = new File(getDataFolder(), "data.yml");

    public FileConfiguration pluginLocalization;
    public File localizationFile = new File(getDataFolder(), "/localization/localization.yml");

    File logFile = new File(getDataFolder(), "one-life.log");
    File deathLogFile = new File(getDataFolder(), "deathlog.log");

    @Override
    public void onEnable() {
        loadFiles();
        registerCommands();
        registerEvents();
        infoUpdater.start();
        replenishmentUtility.start();
    }

    @Override
    public void onDisable() {
        infoUpdater.stop();
        replenishmentUtility.stop();
    }

    private void loadFiles(){
        if (!configFile.getParentFile().exists() || !configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", true);
        }
        if (!dataFile.getParentFile().exists() || !dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (!localizationFile.getParentFile().exists() || !localizationFile.exists()) {
            localizationFile.getParentFile().mkdirs();
            saveResource("en-US.yml", true);
            try {
                if (!Files.exists(localizationFile.toPath())) {
                    saveResource("localization.yml", true);
                    Files.move(Path.of(getDataFolder() + "/localization.yml"), localizationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                Files.move(Path.of(getDataFolder() + "/en-US.yml"), Path.of(getDataFolder().toPath() + "/localization/en-US.yml"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (!Files.exists(Path.of(getDataFolder() + "/localization/en-US.yml"))) {
            saveResource("en-US.yml", true);
            try {
                Files.move(Path.of(getDataFolder() + "/en-US.yml"), Path.of(getDataFolder().toPath() + "/localization/en-US.yml"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        pluginConfig = new YamlConfiguration();
        pluginData = new YamlConfiguration();
        pluginLocalization = new YamlConfiguration();

        try {
            pluginConfig.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().warning("Failed to load the config file: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            pluginData.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Failed to load the data file: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            pluginLocalization.load(localizationFile);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().warning("Failed to load the localization information file: " + e.getMessage());
            e.printStackTrace();
        }

        getLogger().info("Starting localization discovery...");
        loadLocalizations();
    }

    private void registerCommands(){
        getCommand("onelife").setExecutor(new LifeConfig(this));
        getCommand("life").setExecutor(new LifeStats(this));

        getCommand("onelife").setTabCompleter(new OneLifeTab());
    }

    private void registerEvents(){
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new GameModeChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);
    }

    private void loadLocalizations() {
        LocalizationUtility localizationUtility = new LocalizationUtility(this);

        int foundLocalizations = 0;
        ArrayList<String> langList = (ArrayList<String>) pluginLocalization.getList("languages");
        ArrayList<String> missingLocalizations = new ArrayList<>();

        for (String langCode : langList) {
            File langFile = new File(getDataFolder(), "/localization/" + langCode + ".yml");
            if (!langFile.exists()) {
                getLogger().warning("[404] Could not find the localization file for " + langCode);
                missingLocalizations.add(langCode);
            } else {
                getLogger().info("[OK] Successfully recognized localization file for " + langCode);
                foundLocalizations++;
            }
        }
        getLogger().info("Localization discovery complete. Found " + foundLocalizations + " localization files out of " + langList.size() + " specified localizations.");
        if (!missingLocalizations.isEmpty()) {
            getLogger().info("Missing localization files: " + Arrays.toString(missingLocalizations.toArray()));
        }
        localizationUtility.setupCurrentLanguageFile();
    }

    public int resetDefaultLanguageFile() {
        saveResource("en-US.yml", true);
        try {
            Files.move(Path.of(getDataFolder() + "/en-US.yml"), Path.of(getDataFolder().toPath() + "/localization/en-US.yml"), StandardCopyOption.REPLACE_EXISTING);
            return 200;
        } catch (IOException e) {
            e.printStackTrace();
            return 500;
        }
    }

    public void reloadCurrentLanguageFile() {
        localizationUtility.setupCurrentLanguageFile();
    }

    @Override
    public void saveConfig(){
        try {
            pluginConfig.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().warning("An error occurred while trying to save the Configuration File.\nReason: " + e.getMessage());
        }
    }

    public boolean saveData() {
        try {
            pluginData.save(dataFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().warning("An error occurred while trying to save the Data File.\nReason: " + e.getMessage());
            return false;
        }
    }

    public void appendToLog(String msg) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
            bw.append("\n").append(msg);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendToDeathLog(String msg) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(deathLogFile, true));
            bw.append("\n").append(msg);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopUtilities() {
        infoUpdater.stop();
        replenishmentUtility.stop();
    }

    public void startUtilities() {
        infoUpdater.start();
        replenishmentUtility.start();
        reloadConfig();
    }
}
