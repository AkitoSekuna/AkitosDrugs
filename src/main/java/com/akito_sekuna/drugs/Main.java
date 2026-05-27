package com.akito_sekuna.drugs;

import com.akito_sekuna.drugs.addiction.AddictionCommand;
import com.akito_sekuna.drugs.addiction.AddictionManager;
import com.akito_sekuna.drugs.utils.PeaceEffectListener;
import com.akito_sekuna.drugs.utils.DrugEffectListener;
import com.akito_sekuna.drugs.utils.DrugMenuListener;
import com.akito_sekuna.drugs.managers.EffectEngine;
import com.akito_sekuna.drugs.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    public static AddictionManager addictionManager;
    public static SettingsManager settingsManager;

    private static Main instance;

    public static File getPluginFolder() {
        return new File(instance.getServer().getPluginsFolder(), "AkitosPlugins/AkitosDrugs");
    }

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Akito's Drugs enabled!");

        com.akito_sekuna.core.Main.registerAddon("AkitosDrugs", "1.1.0");
        addictionManager = new AddictionManager(this);
        settingsManager = new SettingsManager(this);

        getCommand("akitosdrugs").setExecutor(new MainCommand());
        getCommand("addiction").setExecutor(new AddictionCommand());
        getCommand("akitosdrugs").setTabCompleter(new MainTabCompleter());
        getServer().getPluginManager().registerEvents(new DrugMenuListener(), this);
        getServer().getPluginManager().registerEvents(new DrugEffectListener(), this);
        getServer().getPluginManager().registerEvents(new PeaceEffectListener(), this);

        long decayInterval = Main.settingsManager.getDecayInterval() * 20L;
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            addictionManager.decayAll(settingsManager.getDecayRate());
        }, decayInterval, decayInterval);

        long withdrawalInterval = settingsManager.getWithdrawalInterval() * 20L;
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (String drug : addictionManager.getDrugsForPlayer(player.getUniqueId())) {
                    double score = addictionManager.getScore(player.getUniqueId(), drug);
                    EffectEngine.applyNegatives(player, drug, score);
                }
            }
        }, withdrawalInterval, withdrawalInterval);
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        getLogger().info("Akito's Drugs disabled!");
    }
}