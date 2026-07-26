package com.akito_sekuna.drugs.managers;

import com.akito_sekuna.drugs.effects.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Random;

public class EffectEngine {

    private static final Random RANDOM = new Random();

    public static void applyPositives(Player player, String drug, double score, SettingsManager settings) {
        clearNegatives(player, drug, settings);
        List<DrugEffect> effects = settings.getPositiveEffects(drug);
        double scaling = settings.getPositiveDurationScaling();
        double durationMultiplier = 1.0 - (score / 100.0 * scaling);
        for (DrugEffect effect : effects) {
            int duration = (int) (effect.baseDuration() * durationMultiplier);
            player.addPotionEffect(new PotionEffect(effect.type(), duration, effect.amplifier(), false, true));
        }

        if (settings.isDissociationEnabled(drug)) DissociationEffect.activate(player, settings.getDissociationDuration(drug));
        if (settings.isPeaceEnabled(drug)) PeaceEffect.activate(player, settings.getPeaceDuration(drug));
        if (settings.isAuraEnabled(drug)) AuraEffect.trigger(player, drug, settings);
        if (settings.isTripEnabled(drug)) TripEffect.activate(player, settings.getTripDuration(drug));
        if (settings.isInvShuffleEnabled(drug)) InvShuffleEffect.startSession(player, drug, settings.getTripDuration(drug));
        if (settings.isTeleportEffectEnabled(drug)) {
            double threshold = settings.getTeleportChanceThreshold(drug);
            if (threshold == 0 || TeleportEffect.shouldTrigger(drug, score, settings)) {
                TeleportEffect.trigger(player, drug, settings);
            }
        }
    }

    public static void applyNegatives(Player player, String drug, double score, SettingsManager settings) {
        if (score <= 0) return;
        List<DrugEffect> effects = settings.getNegativeEffects(drug);
        double intensity = score / 100.0;
        double ampScaling = settings.getNegativeAmplifierScaling();
        for (DrugEffect effect : effects) {
            int duration = (int) (effect.baseDuration() * intensity);
            int amplifier = (int) (effect.amplifier() + (intensity * ampScaling));
            player.addPotionEffect(new PotionEffect(effect.type(), duration, amplifier, false, true));
        }
    }

    public static void sendDrugMessages(Player player, String drug, double score, SettingsManager settings) {
        int threshold = settings.getMessageThreshold(drug);
        List<String> pool = score >= threshold ? settings.getMessagesHigh(drug) : settings.getMessagesLow(drug);
        if (pool.isEmpty()) return;
        player.sendMessage(pool.get(RANDOM.nextInt(pool.size())));
    }

    public static void clearNegatives(Player player, String drug, SettingsManager settings) {
        for (DrugEffect effect : settings.getNegativeEffects(drug)) {
            player.removePotionEffect(effect.type());
        }
    }

    public static void sendCleansingMessage(Player player, String drug, SettingsManager settings) {
        List<String> pool = settings.getMessagesCleanse(drug);
        if (pool.isEmpty()) return;
        player.sendMessage(pool.get(RANDOM.nextInt(pool.size())));
    }
}
