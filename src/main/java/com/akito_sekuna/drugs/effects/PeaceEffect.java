package com.akito_sekuna.drugs.effects;

import com.akito_sekuna.drugs.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PeaceEffect {

    private record PeaceData(BukkitTask task) {}

    private static final Map<UUID, PeaceData> active = new HashMap<>();

    public static void activate(Player player, int durationTicks) {
        if (active.containsKey(player.getUniqueId())) return;

        player.sendActionBar("§aA profound sense of peace washes over you...");

        final int[] remaining = {durationTicks / 20};
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            if (!player.isOnline()) {
                deactivate(player.getUniqueId());
                return;
            }
            if (remaining[0] <= 0) {
                deactivate(player.getUniqueId());
                return;
            }
            player.sendActionBar("§aPeace... §7[" + remaining[0] + "s remaining]");
            remaining[0]--;
        }, 20L, 20L);

        active.put(player.getUniqueId(), new PeaceData(task));
    }

    public static void deactivate(UUID uuid) {
        PeaceData data = active.remove(uuid);
        if (data == null) return;
        data.task().cancel();

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            player.sendActionBar("§7The peace fades...");
        }
    }

    public static boolean isActive(UUID uuid) {
        return active.containsKey(uuid);
    }
}