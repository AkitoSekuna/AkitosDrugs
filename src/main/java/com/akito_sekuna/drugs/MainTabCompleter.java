package com.akito_sekuna.drugs;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(List.of("reload", "drugs", "addiction", "reset", "set"));
        }

        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("addiction")) {
                completions.addAll(List.of("reset", "set"));
                completions.addAll(getOnlinePlayers());
            } else if (args[0].equalsIgnoreCase("reset")) {
                completions.addAll(getOnlinePlayers());
            } else if (args[0].equalsIgnoreCase("set")) {
                completions.addAll(getOnlinePlayers());
            }
        }

        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("addiction")) {
                if (args[1].equalsIgnoreCase("reset")) {
                    completions.addAll(getOnlinePlayers());
                } else if (args[1].equalsIgnoreCase("set")) {
                    completions.addAll(getOnlinePlayers());
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                completions.addAll(getDrugNames());
            }
        }

        else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("addiction") && args[1].equalsIgnoreCase("set")) {
                completions.addAll(getDrugNames());
            }
        }

        return filter(completions, args[args.length - 1]);
    }

    private List<String> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    private List<String> getDrugNames() {
        return new ArrayList<>(Main.settingsManager.getDrugNames());
    }

    private List<String> filter(List<String> list, String current) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }
}