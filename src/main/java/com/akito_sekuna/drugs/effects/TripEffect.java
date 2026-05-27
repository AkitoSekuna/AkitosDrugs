package com.akito_sekuna.drugs.effects;

import com.akito_sekuna.drugs.Main;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TripEffect {

    private record TripData(BukkitTask task) {}

    private static final Map<UUID, TripData> active = new HashMap<>();
    private static final Random RANDOM = new Random();

    private static final Color[] RAINBOW = {
            Color.RED, Color.ORANGE, Color.YELLOW,
            Color.LIME, Color.AQUA, Color.BLUE,
            Color.PURPLE, Color.FUCHSIA, Color.WHITE
    };

    public static void activate(Player player, int durationTicks) {
        if (active.containsKey(player.getUniqueId())) return;

        player.sendActionBar("§5The world begins to breathe...");

        final int[] remaining = {durationTicks / 20};
        final int[] colorIndex = {0};

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            if (!player.isOnline() || remaining[0] <= 0) {
                deactivate(player.getUniqueId());
                return;
            }

            // cycle through rainbow colors
            Color color = RAINBOW[colorIndex[0] % RAINBOW.length];
            colorIndex[0]++;

            // spawn dust particles in a sphere around the player
            for (int i = 0; i < 2000; i++) {
                double angle = RANDOM.nextDouble() * Math.PI * 2;
                double radius = 0.5 + RANDOM.nextDouble() * 1.5;
                double dx = Math.cos(angle) * radius;
                double dz = Math.sin(angle) * radius;
                double dy = (RANDOM.nextDouble() * 2) - 0.5;

                Particle.DustOptions dust = new Particle.DustOptions(color, 1.5f);
                player.spawnParticle(
                        Particle.DUST,
                        player.getLocation().add(dx, dy + 1, dz),
                        1, 0, 0, 0, 0, dust
                );
            }

            // random blindness flash
            if (RANDOM.nextInt(10) == 0) {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.BLINDNESS, 15, 0, false, false
                ));
            }

            // action bar countdown every 5 ticks
            if (remaining[0] % 5 == 0) {
                player.sendActionBar("§5Tripping... §7[" + remaining[0] + "s remaining]");
            }

            remaining[0]--;
        }, 0L, 20L);

        active.put(player.getUniqueId(), new TripData(task));
    }

    public static void deactivate(UUID uuid) {
        TripData data = active.remove(uuid);
        if (data == null) return;
        data.task().cancel();

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            player.sendActionBar("§7The world settles back into itself...");
        }
    }
}