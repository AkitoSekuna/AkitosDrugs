package com.akito_sekuna.drugs;

import com.akito_sekuna.core.AkitosAddon;
import com.akito_sekuna.core.ReloadReason;
import com.akito_sekuna.core.api.ICoreAPI;
import com.akito_sekuna.drugs.addiction.AddictionCommand;
import com.akito_sekuna.drugs.addiction.AddictionManager;
import com.akito_sekuna.drugs.managers.EffectEngine;
import com.akito_sekuna.drugs.managers.SettingsManager;
import com.akito_sekuna.drugs.utils.DrugEffectListener;
import com.akito_sekuna.drugs.utils.DrugMenuListener;
import com.akito_sekuna.drugs.utils.PeaceEffectListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin implements AkitosAddon {

    private static Main instance;
    private ICoreAPI coreAPI;

    private AddictionManager addictionManager;
    private SettingsManager settingsManager;

    public static File getPluginFolder() {
        return new File(instance.getServer().getPluginsFolder(), "AkitosPlugins/AkitosDrugs");
    }

    public static Main getInstance() {
        return instance;
    }

    public AddictionManager getAddictionManager() {
        return addictionManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public ICoreAPI getCoreAPI() {
        return coreAPI;
    }

    // --- AkitosAddon ---

    @Override
    public String getAddonName() {
        return "AkitosDrugs";
    }

    @Override
    public String getAddonVersion() {
        return getPluginMeta().getVersion();
    }

    @Override
    public void onCoreReady(ICoreAPI api) {
        this.coreAPI = api;
    }

    @Override
    public void onCoreReload(ICoreAPI newApi, ReloadReason reason) {
        this.coreAPI = newApi;
        settingsManager = new SettingsManager(this);
        addictionManager = new AddictionManager(this);
    }

    @Override
    public void onCoreShutdown() {}

    // --- Lifecycle ---

    @Override
    public void onEnable() {
        instance = this;

        com.akito_sekuna.core.Main.registerAddon(this);

        addictionManager = new AddictionManager(this);
        settingsManager = new SettingsManager(this);

        PluginCommand adCmd = getCommand("akitosdrugs");
        if (adCmd != null) {
            adCmd.setExecutor(new MainCommand(this));
            adCmd.setTabCompleter(new MainTabCompleter(this));
        } else {
            getLogger().severe("Failed to register 'akitosdrugs' command -- check plugin.yml!");
        }

        PluginCommand addictionCmd = getCommand("addiction");
        if (addictionCmd != null) {
            addictionCmd.setExecutor(new AddictionCommand(this));
        } else {
            getLogger().severe("Failed to register 'addiction' command -- check plugin.yml!");
        }

        getServer().getPluginManager().registerEvents(new DrugMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new DrugEffectListener(this), this);
        getServer().getPluginManager().registerEvents(new PeaceEffectListener(), this);

        long decayInterval = settingsManager.getDecayInterval() * 20L;
        Bukkit.getScheduler().runTaskTimer(this, () ->
                addictionManager.decayAll(settingsManager.getDecayRate()), decayInterval, decayInterval);

        long withdrawalInterval = settingsManager.getWithdrawalInterval() * 20L;
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (String drug : addictionManager.getDrugsForPlayer(player.getUniqueId())) {
                    double score = addictionManager.getScore(player.getUniqueId(), drug);
                    EffectEngine.applyNegatives(player, drug, score, settingsManager);
                }
            }
        }, withdrawalInterval, withdrawalInterval);

        getLogger().info("AkitosDrugs v" + getPluginMeta().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AkitosDrugs disabled!");
    }
}
