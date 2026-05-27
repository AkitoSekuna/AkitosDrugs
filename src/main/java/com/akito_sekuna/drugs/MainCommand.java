package com.akito_sekuna.drugs;

import com.akito_sekuna.drugs.addiction.AddictionCommand;
import com.akito_sekuna.drugs.addiction.AddictionManager;
import com.akito_sekuna.drugs.utils.DrugMenu;
import com.akito_sekuna.drugs.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        // /ad reload
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("akitosdrugs.admin.reload")) {
                sender.sendMessage("§cYou don't have permission to do this!");
                return true;
            }
            Main.settingsManager = new SettingsManager(Main.getInstance());
            Main.addictionManager = new AddictionManager(Main.getInstance());
            sender.sendMessage("§aAkitosDrugs reloaded!");
            return true;
        }

        // /ad drugs
        if (args[0].equalsIgnoreCase("drugs")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be used by players!");
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("akitosdrugs.admin.drugs")) {
                player.sendMessage("§cYou don't have permission to do this!");
                return true;
            }
            DrugMenu.open(player);
            return true;
        }

        // /ad addiction ...
        if (args[0].equalsIgnoreCase("addiction")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be used by players!");
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("akitosdrugs.admin.addiction")) {
                player.sendMessage("§cYou don't have permission to do this!");
                return true;
            }

            // /ad addiction <player>
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { player.sendMessage("§cPlayer not found!"); return true; }
                player.sendMessage("§8--- §c" + target.getName() + "'s Addictions §8---");
                AddictionCommand.showAddiction(player, target.getUniqueId());
                return true;
            }

            // /ad addiction reset <player>
            if (args.length == 3 && args[1].equalsIgnoreCase("reset")) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) { player.sendMessage("§cPlayer not found!"); return true; }
                Main.addictionManager.resetPlayer(target.getUniqueId());
                player.sendMessage("§aReset all addiction for §f" + target.getName() + "§a.");
                target.sendMessage("§aYour addiction has been reset by an admin.");
                return true;
            }

            // /ad addiction set <player> <drug> <amount>
            if (args.length == 5 && args[1].equalsIgnoreCase("set")) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) { player.sendMessage("§cPlayer not found!"); return true; }
                String drug = args[3];
                if (!Main.settingsManager.getDrugNames().contains(drug)) {
                    player.sendMessage("§cUnknown drug: §f" + drug);
                    return true;
                }
                double amount;
                try {
                    amount = Double.parseDouble(args[4]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cInvalid amount!");
                    return true;
                }
                Main.addictionManager.setScore(target.getUniqueId(), drug, amount);
                player.sendMessage("§aSet §f" + target.getName() + "§a's §f" + drug + "§a addiction to §f" + amount + "§a.");
                target.sendMessage("§cAn admin has modified your " + drug + " addiction.");
                return true;
            }

            sendHelp(sender);
            return true;
        }

        // /ad reset <player>
        if (args[0].equalsIgnoreCase("reset") && args.length == 2) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            if (!player.hasPermission("akitosdrugs.admin.addiction")) {
                sender.sendMessage("§cYou don't have permission to do this!");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) { sender.sendMessage("§cPlayer not found!"); return true; }
            Main.addictionManager.resetPlayer(target.getUniqueId());
            sender.sendMessage("§aReset all addiction for §f" + target.getName() + "§a.");
            target.sendMessage("§aYour addiction has been reset by an admin.");
            return true;
        }

        // /ad set <player> <drug> <amount>
        if (args[0].equalsIgnoreCase("set") && args.length == 4) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            if (!player.hasPermission("akitosdrugs.admin.addiction")) {
                sender.sendMessage("§cYou don't have permission to do this!");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) { sender.sendMessage("§cPlayer not found!"); return true; }
            String drug = args[2];
            if (!Main.settingsManager.getDrugNames().contains(drug)) {
                sender.sendMessage("§cUnknown drug: §f" + drug);
                return true;
            }
            double amount;
            try {
                amount = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount!");
                return true;
            }
            Main.addictionManager.setScore(target.getUniqueId(), drug, amount);
            sender.sendMessage("§aSet §f" + target.getName() + "§a's §f" + drug + "§a addiction to §f" + amount + "§a.");
            target.sendMessage("§cAn admin has modified your " + drug + " addiction.");
            return true;
        }

        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§8--- §bAkito's Drugs §8---");
        sender.sendMessage("§7/ad reload §8- §7Reload config");
        sender.sendMessage("§7/ad drugs §8- §7Open drug menu");
        sender.sendMessage("§7/ad addiction <player> §8- §7View player addiction");
        sender.sendMessage("§7/ad addiction reset <player> §8- §7Reset player addiction");
        sender.sendMessage("§7/ad addiction set <player> <drug> <amount> §8- §7Set addiction score");
        sender.sendMessage("§7/ad reset <player> §8- §7Reset player addiction (shortcut)");
        sender.sendMessage("§7/ad set <player> <drug> <amount> §8- §7Set addiction score (shortcut)");
    }
}