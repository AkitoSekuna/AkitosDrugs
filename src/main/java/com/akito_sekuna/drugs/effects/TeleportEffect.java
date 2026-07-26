package com.akito_sekuna.drugs.effects;

import com.akito_sekuna.drugs.managers.SettingsManager;
import com.akito_sekuna.drugs.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class TeleportEffect {

    private static final Random RANDOM = new Random();

    public static void trigger(Player player, String drug, SettingsManager settings) {
        int radius = settings.getTeleportRadius(drug);
        boolean cameraRotation = settings.isCameraRotationEnabled(drug);
        boolean invShuffle = settings.isTeleportInvShuffle(drug);
        boolean blindness = settings.isTeleportBlindness(drug);
        boolean nausea = settings.isTeleportNausea(drug);
        int durationTicks = settings.getTeleportDuration(drug) * 20;

        if (blindness) player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, durationTicks, 1, false, false));
        if (nausea) player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, durationTicks, 0, false, false));
        if (invShuffle) InvShuffleEffect.shuffle(player);

        Location safe = LocationUtil.findSafeLocation(player.getLocation(), radius);
        if (safe == null) safe = player.getLocation();

        if (cameraRotation) {
            safe.setYaw(RANDOM.nextFloat() * 360);
            safe.setPitch((RANDOM.nextFloat() * 60) - 30);
        }

        player.teleport(safe);
        player.sendActionBar("§cReality slips away...");
    }

    public static boolean shouldTrigger(String drug, double score, SettingsManager settings) {
        double threshold = settings.getTeleportChanceThreshold(drug);
        if (score < threshold) return false;
        double chance = (score - threshold) / (100 - threshold) * 0.20 + 0.05;
        return RANDOM.nextDouble() < chance;
    }
}
