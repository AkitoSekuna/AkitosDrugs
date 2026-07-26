package com.akito_sekuna.drugs.addiction;

import com.akito_sekuna.drugs.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AddictionCommand implements CommandExecutor {

    private final Main plugin;

    public AddictionCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        showAddiction(player, player.getUniqueId(), plugin);
        return true;
    }

    public static void showAddiction(Player viewer, UUID target, Main plugin) {
        AddictionManager manager = plugin.getAddictionManager();
        if (manager.getDrugsForPlayer(target).isEmpty()) {
            viewer.sendMessage("§aNo active addictions.");
            return;
        }
        viewer.sendMessage("§8--- §cAddictions §8---");
        for (String drug : manager.getDrugsForPlayer(target)) {
            double score = manager.getScore(target, drug);
            viewer.sendMessage("§7" + drug + ": §c" + String.format("%.1f", score) + "§7/100");
        }
    }
}
