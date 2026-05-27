package com.akito_sekuna.drugs.managers;

import com.akito_sekuna.drugs.Main;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class SettingsManager {

    private final FileConfiguration config;

    public SettingsManager(JavaPlugin plugin) {
        File file = new File(Main.getPluginFolder(), "settings.yml");
        if (!file.exists()) {
            Main.getPluginFolder().mkdirs();
            try (InputStream in = plugin.getResource("settings.yml")) {
                if (in != null) Files.copy(in, file.toPath());
            } catch (Exception e) { e.printStackTrace(); }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    // --- Drug metadata ---

    public Set<String> getDrugNames() {
        if (!config.contains("drugs")) return new LinkedHashSet<>();
        return new LinkedHashSet<>(config.getConfigurationSection("drugs").getKeys(false));
    }

    public String getDisplayName(String drug) {
        return config.getString("drugs." + drug + ".display-name", drug);
    }

    public Material getItem(String drug) {
        String mat = config.getString("drugs." + drug + ".item", "PAPER");
        try { return Material.valueOf(mat); } catch (Exception e) { return Material.PAPER; }
    }

    public String getCategory(String drug) {
        return config.getString("drugs." + drug + ".category", "illegal");
    }

    public List<String> getLore(String drug) {
        return config.getStringList("drugs." + drug + ".lore");
    }

    // --- Addiction ---

    public double getAddictionPerUse(String drug) {
        return config.getDouble("drugs." + drug + ".addiction-per-use", 5.0);
    }

    public double getDecayRate() {
        return config.getDouble("addiction.decay-rate", 0.5);
    }

    public int getDecayInterval() {
        return config.getInt("addiction.decay-interval-seconds", 30);
    }

    public int getWithdrawalInterval() {
        return config.getInt("addiction.withdrawal-interval-seconds", 5);
    }

    public double getPositiveDurationScaling() {
        return config.getDouble("addiction.positive-duration-scaling", 0.9);
    }

    public double getNegativeAmplifierScaling() {
        return config.getDouble("addiction.negative-amplifier-scaling", 2.0);
    }

    public int getMessageThreshold(String drug) {
        return config.getInt("drugs." + drug + ".message-threshold", 50);
    }

    // --- Effects ---

    public List<DrugEffect> getPositiveEffects(String drug) {
        return parseEffects("drugs." + drug + ".positive-effects");
    }

    public List<DrugEffect> getNegativeEffects(String drug) {
        return parseEffects("drugs." + drug + ".negative-effects");
    }

    public boolean isDissociationEnabled(String drug) {
        return config.getBoolean("drugs." + drug + ".dissociation.enabled", false);
    }

    public int getDissociationDuration(String drug) {
        return config.getInt("drugs." + drug + ".dissociation.duration", 200);
    }

    public boolean isPeaceEnabled(String drug) {
        return config.getBoolean("drugs." + drug + ".peace.enabled", false);
    }

    public int getPeaceDuration(String drug) {
        return config.getInt("drugs." + drug + ".peace.duration", 200);
    }

    public boolean isAuraEnabled(String drug) {
        return config.getBoolean("drugs." + drug + ".aura.enabled", false);
    }

    public int getAuraRadius(String drug) {
        return config.getInt("drugs." + drug + ".aura.radius", 5);
    }

    public List<DrugEffect> getAuraEffects(String drug) {
        return parseEffects("drugs." + drug + ".aura.effects");
    }

    public boolean isTripEnabled(String drug) {
        return config.getBoolean("drugs." + drug + ".trip.enabled", false);
    }

    public int getTripDuration(String drug) {
        return config.getInt("drugs." + drug + ".trip.duration", 200);
    }

    public int getPassiveShuffleThreshold(String drug) {
        return config.getInt("drugs." + drug + ".inv-shuffle.passive-threshold", 70);
    }

    public boolean isInvShuffleEnabled(String drug) {
        return config.getBoolean("drugs." + drug + ".inv-shuffle.enabled", false);
    }

    public int getInvShuffleInterval(String drug) {
        return config.getInt("drugs." + drug + ".inv-shuffle.interval-seconds", 30);
    }

    public boolean isTeleportEffectEnabled(String drug) {
        return config.getBoolean("drugs." + drug + ".teleport-effect.enabled", false);
    }

    public int getTeleportRadius(String drug) {
        return config.getInt("drugs." + drug + ".teleport-effect.tp-radius", 10);
    }

    public boolean isCameraRotationEnabled(String drug) {
        return config.getBoolean("drugs." + drug + ".teleport-effect.camera-rotation", false);
    }

    public boolean isTeleportInvShuffle(String drug) {
        return config.getBoolean("drugs." + drug + ".teleport-effect.inv-shuffle", false);
    }

    public boolean isTeleportBlindness(String drug) {
        return config.getBoolean("drugs." + drug + ".teleport-effect.blindness", true);
    }

    public boolean isTeleportNausea(String drug) {
        return config.getBoolean("drugs." + drug + ".teleport-effect.nausea", false);
    }

    public int getTeleportDuration(String drug) {
        return config.getInt("drugs." + drug + ".teleport-effect.duration-seconds", 5);
    }

    public double getTeleportChanceThreshold(String drug) {
        return config.getDouble("drugs." + drug + ".teleport-effect.chance-threshold", 80);
    }

    public int getAuraDuration(String drug) {
        return config.getInt("drugs." + drug + ".aura.duration", 60);
    }

    // --- Particles ---

    public String getParticleType(String drug) {
        return config.getString("drugs." + drug + ".particles.type", "SNEEZE");
    }

    public int getParticleCount(String drug) {
        return config.getInt("drugs." + drug + ".particles.count", 30);
    }

    public double getParticleSpread(String drug) {
        return config.getDouble("drugs." + drug + ".particles.spread", 0.5);
    }

    public double getParticleSpeed(String drug) {
        return config.getDouble("drugs." + drug + ".particles.speed", 0.1);
    }

    // --- Messages ---

    public List<String> getMessagesLow(String drug) {
        return config.getStringList("drugs." + drug + ".messages-low");
    }

    public List<String> getMessagesHigh(String drug) {
        return config.getStringList("drugs." + drug + ".messages-high");
    }

    public List<String> getMessagesCleanse(String drug) {
        return config.getStringList("drugs." + drug + ".messages-cleanse");
    }

    // --- Cooldown ---
    public int getUseCooldown(String drug) {
        return config.getInt("drugs." + drug + ".use-cooldown-seconds", 30);
    }
    // --- Internal ---

    private List<DrugEffect> parseEffects(String path) {
        List<String> raw = config.getStringList(path);
        List<DrugEffect> effects = new ArrayList<>();
        for (String entry : raw) {
            String[] parts = entry.split(",");
            if (parts.length != 3) continue;
            try {
                PotionEffectType type = PotionEffectType.getByName(parts[0].trim());
                int amplifier = Integer.parseInt(parts[1].trim());
                int baseDuration = Integer.parseInt(parts[2].trim());
                if (type != null) effects.add(new DrugEffect(type, amplifier, baseDuration));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return effects;
    }
}