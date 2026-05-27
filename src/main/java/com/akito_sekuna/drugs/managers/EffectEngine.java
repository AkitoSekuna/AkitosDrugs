package com.akito_sekuna.drugs.managers;

import com.akito_sekuna.drugs.Main;
import com.akito_sekuna.drugs.effects.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Random;

public class EffectEngine {

    private static final Random RANDOM = new Random();

    public static void applyPositives(Player player, String drug, double score) {
        clearNegatives(player, drug);
        List<DrugEffect> effects = Main.settingsManager.getPositiveEffects(drug);
        double scaling = Main.settingsManager.getPositiveDurationScaling();
        double durationMultiplier = 1.0 - (score / 100.0 * scaling);
        for (DrugEffect effect : effects) {
            int duration = (int)(effect.baseDuration() * durationMultiplier);
            player.addPotionEffect(new PotionEffect(effect.type(), duration, effect.amplifier(), false, true));
        }

        if (Main.settingsManager.isDissociationEnabled(drug)) {
            int duration = Main.settingsManager.getDissociationDuration(drug);
            DissociationEffect.activate(player, duration);
        }

        if (Main.settingsManager.isPeaceEnabled(drug)) {
            int duration = Main.settingsManager.getPeaceDuration(drug);
            PeaceEffect.activate(player, duration);
        }

        if (Main.settingsManager.isAuraEnabled(drug)) {
            AuraEffect.trigger(player, drug);
        }

        if (Main.settingsManager.isTripEnabled(drug)) {
            TripEffect.activate(player, Main.settingsManager.getTripDuration(drug));
        }

        if (Main.settingsManager.isInvShuffleEnabled(drug)) {
            int duration = Main.settingsManager.getTripDuration(drug);
            InvShuffleEffect.startSession(player, drug, duration);
        }

        if (Main.settingsManager.isTeleportEffectEnabled(drug)) {
            double threshold = Main.settingsManager.getTeleportChanceThreshold(drug);
            if (threshold == 0 || TeleportEffect.shouldTrigger(drug, score)) {
                TeleportEffect.trigger(player, drug);
            }
        }
    }

    public static void applyNegatives(Player player, String drug, double score) {
        if (score <= 0) return;
        List<DrugEffect> effects = Main.settingsManager.getNegativeEffects(drug);
        double intensity = score / 100.0;
        double ampScaling = Main.settingsManager.getNegativeAmplifierScaling();
        for (DrugEffect effect : effects) {
            int duration = (int)(effect.baseDuration() * intensity);
            int amplifier = (int)(effect.amplifier() + (intensity * ampScaling));
            player.addPotionEffect(new PotionEffect(effect.type(), duration, amplifier, false, true));
        }
    }

    public static void sendDrugMessages(Player player, String drug, double score) {
        int threshold = Main.settingsManager.getMessageThreshold(drug);
        List<String> pool = score >= threshold
                ? Main.settingsManager.getMessagesHigh(drug)
                : Main.settingsManager.getMessagesLow(drug);
        if (pool.isEmpty()) return;
        player.sendMessage(pool.get(RANDOM.nextInt(pool.size())));
    }

    public static void clearNegatives(Player player, String drug) {
        List<DrugEffect> effects = Main.settingsManager.getNegativeEffects(drug);
        for (DrugEffect effect : effects) {
            player.removePotionEffect(effect.type());
        }
    }

    public static void sendCleansingMessage(Player player, String drug) {
        List<String> pool = Main.settingsManager.getMessagesCleanse(drug);
        if (pool.isEmpty()) return;
        player.sendMessage(pool.get(RANDOM.nextInt(pool.size())));
    }
}