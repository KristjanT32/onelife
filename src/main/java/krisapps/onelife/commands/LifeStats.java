package krisapps.onelife.commands;

import krisapps.onelife.OneLife;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LifeStats implements CommandExecutor {

    OneLife main;
    public LifeStats(OneLife main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Syntax: /life
        if (sender instanceof Player) {
            if (main.dataUtility.hasDied(((Player) sender).getPlayer())) { return true; }
            main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.life.response")
                    .replaceAll("%lives%", String.valueOf(main.dataUtility.getLives(((Player) sender).getPlayer())))
                    .replaceAll("%deaths%", String.valueOf(main.dataUtility.getDeaths(((Player) sender).getPlayer())))
            );
        } else {
            main.messageUtility.sendMessage(sender, main.localizationUtility.getLocalizedPhrase("commands.playeronly"));
        }

        return true;
    }
}
