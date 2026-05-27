package com.akito_sekuna.drugs.addiction;

import com.akito_sekuna.drugs.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class AddictionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        showAddiction(player, player.getUniqueId());
        return true;
    }

    public static void showAddiction(Player viewer, UUID target) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(
                new File(Main.getInstance().getDataFolder(), "addiction.yml")
        );
        if (!config.contains(target.toString())) {
            viewer.sendMessage("§aNo active addictions.");
            return;
        }
        viewer.sendMessage("§8--- §cAddictions §8---");
        for (String drug : config.getConfigurationSection(target.toString()).getKeys(false)) {
            double score = config.getDouble(target + "." + drug);
            viewer.sendMessage("§7" + drug + ": §c" + String.format("%.1f", score) + "§7/100");
        }
    }
}