package com.akito_sekuna.drugs.effects;

import com.akito_sekuna.drugs.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class InvShuffleEffect {

    private static final Map<UUID, BukkitTask> active = new HashMap<>();

    public static void shuffle(Player player) {
        ItemStack[] contents = player.getInventory().getStorageContents();
        List<ItemStack> items = Arrays.asList(contents);
        Collections.shuffle(items);
        player.getInventory().setStorageContents(items.toArray(new ItemStack[0]));
        player.sendMessage("§cYour thoughts scramble your pockets...");
    }

    public static void startSession(Player player, String drug, int durationTicks) {
        if (active.containsKey(player.getUniqueId())) return;

        int intervalTicks = Main.settingsManager.getInvShuffleInterval(drug) * 20;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            if (!player.isOnline() || !active.containsKey(player.getUniqueId())) {
                stopSession(player.getUniqueId());
                return;
            }
            shuffle(player);
        }, intervalTicks, intervalTicks);

        active.put(player.getUniqueId(), task);

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () ->
                stopSession(player.getUniqueId()), durationTicks);
    }

    public static void stopSession(UUID uuid) {
        BukkitTask task = active.remove(uuid);
        if (task != null) task.cancel();
    }
}