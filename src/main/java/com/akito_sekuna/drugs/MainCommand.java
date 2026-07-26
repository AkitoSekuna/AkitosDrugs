package com.akito_sekuna.drugs;

import com.akito_sekuna.drugs.addiction.AddictionCommand;
import com.akito_sekuna.drugs.utils.DrugMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {

    private final Main plugin;

    public MainCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) { sendHelp(sender); return true; }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("akitosdrugs.admin.reload")) {
                sender.sendMessage("§cYou don't have permission to do this!");
                return true;
            }
            plugin.onCoreReload(plugin.getCoreAPI(), com.akito_sekuna.core.ReloadReason.ADMIN_COMMAND);
            sender.sendMessage("§aAkitosDrugs reloaded!");
            return true;
        }

        if (args[0].equalsIgnoreCase("drugs")) {
            if (!(sender instanceof Player player)) { sender.sendMessage("Players only!"); return true; }
            if (!player.hasPermission("akitosdrugs.admin.drugs")) { player.sendMessage("§cNo permission!"); return true; }
            DrugMenu.open(player, plugin);
            return true;
        }

        if (args[0].equalsIgnoreCase("addiction")) {
            if (!(sender instanceof Player player)) { sender.sendMessage("Players only!"); return true; }
            if (!player.hasPermission("akitosdrugs.admin.addiction")) { player.sendMessage("§cNo permission!"); return true; }
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { player.sendMessage("§cPlayer not found!"); return true; }
                player.sendMessage("§8--- §c" + target.getName() + "'s Addictions §8---");
                AddictionCommand.showAddiction(player, target.getUniqueId(), plugin);
                return true;
            }
            if (args.length == 3 && args[1].equalsIgnoreCase("reset")) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) { player.sendMessage("§cPlayer not found!"); return true; }
                plugin.getAddictionManager().resetPlayer(target.getUniqueId());
                player.sendMessage("§aReset §f" + target.getName() + "§a's addiction.");
                target.sendMessage("§aYour addiction has been reset by an admin.");
                return true;
            }
            if (args.length == 5 && args[1].equalsIgnoreCase("set")) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) { player.sendMessage("§cPlayer not found!"); return true; }
                String drug = args[3];
                if (!plugin.getSettingsManager().getDrugNames().contains(drug)) { player.sendMessage("§cUnknown drug: §f" + drug); return true; }
                try {
                    double amount = Double.parseDouble(args[4]);
                    plugin.getAddictionManager().setScore(target.getUniqueId(), drug, amount);
                    player.sendMessage("§aSet §f" + target.getName() + "§a's §f" + drug + "§a addiction to §f" + amount + "§a.");
                    target.sendMessage("§cAn admin has modified your " + drug + " addiction.");
                } catch (NumberFormatException e) { player.sendMessage("§cInvalid amount!"); }
                return true;
            }
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reset") && args.length == 2) {
            if (!sender.hasPermission("akitosdrugs.admin.addiction")) { sender.sendMessage("§cNo permission!"); return true; }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) { sender.sendMessage("§cPlayer not found!"); return true; }
            plugin.getAddictionManager().resetPlayer(target.getUniqueId());
            sender.sendMessage("§aReset §f" + target.getName() + "§a's addiction.");
            target.sendMessage("§aYour addiction has been reset by an admin.");
            return true;
        }

        if (args[0].equalsIgnoreCase("set") && args.length == 4) {
            if (!sender.hasPermission("akitosdrugs.admin.addiction")) { sender.sendMessage("§cNo permission!"); return true; }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) { sender.sendMessage("§cPlayer not found!"); return true; }
            String drug = args[2];
            if (!plugin.getSettingsManager().getDrugNames().contains(drug)) { sender.sendMessage("§cUnknown drug: §f" + drug); return true; }
            try {
                double amount = Double.parseDouble(args[3]);
                plugin.getAddictionManager().setScore(target.getUniqueId(), drug, amount);
                sender.sendMessage("§aSet §f" + target.getName() + "§a's §f" + drug + "§a addiction to §f" + amount + "§a.");
                target.sendMessage("§cAn admin has modified your " + drug + " addiction.");
            } catch (NumberFormatException e) { sender.sendMessage("§cInvalid amount!"); }
            return true;
        }

        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§8--- §bAkito's Drugs §8---");
        sender.sendMessage("§7/ad reload §8- §7Reload config");
        sender.sendMessage("§7/ad drugs §8- §7Open drug menu");
        sender.sendMessage("§7/ad addiction <player> §8- §7View addiction");
        sender.sendMessage("§7/ad addiction reset <player> §8- §7Reset addiction");
        sender.sendMessage("§7/ad addiction set <player> <drug> <amount> §8- §7Set addiction");
        sender.sendMessage("§7/ad reset <player> §8- §7Reset addiction (shortcut)");
        sender.sendMessage("§7/ad set <player> <drug> <amount> §8- §7Set addiction (shortcut)");
    }
}
