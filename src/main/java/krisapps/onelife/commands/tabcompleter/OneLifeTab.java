package krisapps.onelife.commands.tabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OneLifeTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("enable", "disable", "setlives", "revive", "forcedeplete", "getstat"));
        } else if (args.length == 2) {
            switch (args[0]) {
                case "setlives":
                case "revive":
                case "forcedeplete":
                case "getstat":
                    completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
                    break;
            }
        } else if (args.length == 3) {
            switch (args[0]) {
                case "setlives":
                    completions.add("<lives>");
                    break;
            }
        }

        return completions;
    }
}
