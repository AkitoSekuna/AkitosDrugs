package com.akito_sekuna.drugs.addiction;

import com.akito_sekuna.drugs.Main;
import com.akito_sekuna.drugs.effects.InvShuffleEffect;
import com.akito_sekuna.drugs.managers.EffectEngine;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddictionManager {

    private final Main plugin;
    private final File file;
    private final FileConfiguration config;

    public AddictionManager(Main plugin) {
        this.plugin = plugin;
        file = new File(Main.getPluginFolder(), "addiction.yml");
        if (!file.exists()) {
            if (!Main.getPluginFolder().mkdirs()) {
                plugin.getLogger().warning("Plugin folder already exists or could not be created.");
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create addiction.yml: " + e.getMessage());
            }
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
            UUID uuid = UUID.fromString(uuidKey);
            for (String drug : config.getConfigurationSection(uuidKey).getKeys(false)) {
                double current = config.getDouble(uuidKey + "." + drug);
                if (current <= 0) continue;

                setScore(uuid, drug, current - rate);
                double newScore = getScore(uuid, drug);

                int oldMilestone = (int) (current / 10);
                int newMilestone = (int) (newScore / 10);

                if (newMilestone < oldMilestone) {
                    org.bukkit.entity.Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        if (newScore <= 0) {
                            player.sendMessage("§l§aI am no longer addicted to " + drug + ".");
                        } else {
                            EffectEngine.sendCleansingMessage(player, drug, plugin.getSettingsManager());
                        }
                    }
                }

                if (plugin.getSettingsManager().isInvShuffleEnabled(drug) &&
                        current >= plugin.getSettingsManager().getPassiveShuffleThreshold(drug)) {
                    org.bukkit.entity.Player player = Bukkit.getPlayer(uuid);
                    if (player != null) InvShuffleEffect.shuffle(player);
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
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save addiction.yml: " + e.getMessage());
        }
    }
}
