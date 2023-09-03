package krisapps.onelife.commands;

import krisapps.onelife.OneLife;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LiveConfig implements CommandExecutor {

    OneLife main;
    public LiveConfig(OneLife main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Syntax: /onelife <enable|disable|setlives|revive|forcedeplete|getstat> <player> <num|none>
        String operation = args[0];
        if (args.length >= 1){
            switch (operation) {
                case "enable":
                    main.dataUtility.setEnabled(true);
                    main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.enabled"));
                    break;
                case "disable":
                    main.dataUtility.setEnabled(false);
                    main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.disabled"));
                    break;
                case "setlives":
                    if (args.length >= 3){
                        String playerName = args[1];
                        int lives = Integer.parseInt(args[2]);
                        Player player = Bukkit.getPlayer(playerName);

                        if (lives <= 0) {
                            main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.less-or-zero")
                                    .replaceAll("%num%", String.valueOf(lives))
                            );
                        }

                        if (player != null) {
                            main.dataUtility.setLives(player, lives);
                            main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.lives-set")
                                    .replaceAll("%lives%", String.valueOf(lives))
                                    .replaceAll("%player%", playerName)
                            );
                        } else {
                            main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.playernotfound")
                                    .replaceAll("%player%", playerName)
                            );
                        }
                    }
                    break;
                case "revive":
                    if (args.length >= 2) {
                        String playerName = args[1];
                        Player player = Bukkit.getPlayer(playerName);

                        if (player != null) {
                            if (main.dataUtility.getLives(player) <= 0) {
                                main.dataUtility.setLives(player, main.dataUtility.getDefaultLifeCount());
                                main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.revived")
                                        .replaceAll("%player%", playerName)
                                        .replaceAll("%lives%", String.valueOf(main.dataUtility.getDefaultLifeCount()))
                                );
                            }
                        } else {
                            main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.playernotfound")
                                    .replaceAll("%player%", playerName)
                            );
                        }
                    }
                    break;
                case "forcedeplete":
                    if (args.length >= 2) {
                        String playerName = args[1];
                        Player player = Bukkit.getPlayer(playerName);

                        if (player != null) {
                            if (main.dataUtility.getLives(player) <= 0) {
                                main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.dead")
                                        .replaceAll("%player%", playerName)
                                );
                            } else {
                                main.dataUtility.depleteLife(player, 1, true);
                                main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.deplete-done")
                                        .replaceAll("%player%", playerName)
                                        .replaceAll("%num%", String.valueOf(main.dataUtility.getLives(player)))
                                );
                            }
                        } else {
                            main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.playernotfound")
                                    .replaceAll("%player%", playerName)
                            );
                        }
                    }
                    break;
                case "getstat":
                    if (args.length >= 2) {
                        String playerName = args[1];
                        Player player = Bukkit.getPlayer(playerName);

                        if (player != null) {

                            if (main.dataUtility.hasOneLifeRecord(player)){
                                main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.getstat-response")
                                        .replaceAll("%uuid%", player.getUniqueId().toString())
                                        .replaceAll("%livesLeft%", String.valueOf(main.dataUtility.getLives(player)))
                                        .replaceAll("%deaths%", String.valueOf(main.dataUtility.getDeaths(player)))
                                );
                            } else {
                                main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.getstat-none")
                                        .replaceAll("%player%", playerName)
                                );
                            }

                        } else {
                            main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.onelife.playernotfound")
                                    .replaceAll("%player%", playerName)
                            );
                        }
                    }
                    break;
            }
        }
        return false;
    }
}
