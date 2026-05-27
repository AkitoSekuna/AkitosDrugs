package com.akito_sekuna.drugs.effects;

import com.akito_sekuna.drugs.Main;
import com.akito_sekuna.drugs.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class TeleportEffect {

    private static final Random RANDOM = new Random();

    public static void trigger(Player player, String drug) {
        int radius = Main.settingsManager.getTeleportRadius(drug);
        boolean cameraRotation = Main.settingsManager.isCameraRotationEnabled(drug);
        boolean invShuffle = Main.settingsManager.isTeleportInvShuffle(drug);
        boolean blindness = Main.settingsManager.isTeleportBlindness(drug);
        boolean nausea = Main.settingsManager.isTeleportNausea(drug);
        int durationSeconds = Main.settingsManager.getTeleportDuration(drug);
        int durationTicks = durationSeconds * 20;

        // apply blindness
        if (blindness) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, durationTicks, 1, false, false));
        }

        // apply nausea
        if (nausea) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, durationTicks, 0, false, false));
        }

        // shuffle inventory
        if (invShuffle) {
            InvShuffleEffect.shuffle(player);
        }

        // find safe location and teleport
        Location safe = LocationUtil.findSafeLocation(player.getLocation(), radius);
        if (safe == null) safe = player.getLocation();

        // camera rotation
        if (cameraRotation) {
            safe.setYaw(RANDOM.nextFloat() * 360);
            safe.setPitch((RANDOM.nextFloat() * 60) - 30);
        }

        final Location dest = safe;
        player.teleport(dest);
        player.sendActionBar("§cReality slips away...");
    }

    public static boolean shouldTrigger(String drug, double score) {
        double threshold = Main.settingsManager.getTeleportChanceThreshold(drug);
        if (score < threshold) return false;
        double chance = (score - threshold) / (100 - threshold) * 0.20 + 0.05;
        return RANDOM.nextDouble() < chance;
    }
}