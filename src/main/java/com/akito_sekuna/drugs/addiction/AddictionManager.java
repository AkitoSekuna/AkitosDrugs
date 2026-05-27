package com.akito_sekuna.drugs.addiction;

import com.akito_sekuna.drugs.Main;
import com.akito_sekuna.drugs.effects.InvShuffleEffect;
import com.akito_sekuna.drugs.managers.EffectEngine;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddictionManager {

    private final File file;
    private final FileConfiguration config;

    public AddictionManager(JavaPlugin plugin) {
        file = new File(Main.getPluginFolder(), "addiction.yml");
        if (!file.exists()) {
            Main.getPluginFolder().mkdirs();
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public double getScore(UUID uuid, String drug) {
        return config.getDouble(uuid + "." + drug, 0.0);
    }

    public void setScore(UUID uuid, String drug, double score) {
        double clamped = Math.max(0, Math.min(100, score));
        if (clamped == 0) {
            config.set(uuid + "." + drug, null);
            // clean up empty player section
            if (config.contains(uuid.toString()) &&
                    config.getConfigurationSection(uuid.toString()).getKeys(false).isEmpty()) {
                config.set(uuid.toString(), null);
            }
        } else {
            config.set(uuid + "." + drug, clamped);
        }
        save();
    }

    public void decayAll(double rate) {
        for (String uuidKey : config.getKeys(false)) {
            for (String drug : config.getConfigurationSection(uuidKey).getKeys(false)) {
                double current = config.getDouble(uuidKey + "." + drug);
                if (current > 0) {
                    setScore(UUID.fromString(uuidKey), drug, current - rate);

                    double newScore = getScore(UUID.fromString(uuidKey), drug);
                    int oldMilestone = (int)(current / 10);
                    int newMilestone = (int)(newScore / 10);

                    if (newMilestone < oldMilestone) {
                        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(UUID.fromString(uuidKey));
                        if (player != null) {
                            if (newScore <= 0) {
                                player.sendMessage("§l§aI am no longer addicted to " + drug + ".");
                            } else {
                                EffectEngine.sendCleansingMessage(player, drug);
                            }
                        }
                    }

                    if (Main.settingsManager.isInvShuffleEnabled(drug) &&
                            current >= Main.settingsManager.getPassiveShuffleThreshold(drug)) {
                        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(UUID.fromString(uuidKey));
                        if (player != null) InvShuffleEffect.shuffle(player);
                    }

                    if (current >= Main.settingsManager.getPassiveShuffleThreshold(drug)) {
                        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(UUID.fromString(uuidKey));
                        if (player != null) InvShuffleEffect.shuffle(player);
                    }
                }
            }
        }
    }

    public void resetPlayer(UUID uuid) {
        config.set(uuid.toString(), null);
        save();
    }

    public List<String> getDrugsForPlayer(UUID uuid) {
        if (!config.contains(uuid.toString())) return new ArrayList<>();
        return new ArrayList<>(config.getConfigurationSection(uuid.toString()).getKeys(false));
    }

    public void addScore(UUID uuid, String drug, double amount) {
        setScore(uuid, drug, getScore(uuid, drug) + amount);
    }

    private void save() {
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}