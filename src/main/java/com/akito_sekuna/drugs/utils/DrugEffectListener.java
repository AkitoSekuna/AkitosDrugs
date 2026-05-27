package com.akito_sekuna.drugs.utils;

import com.akito_sekuna.drugs.Main;
import com.akito_sekuna.drugs.managers.EffectEngine;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DrugEffectListener implements Listener {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.getAction().isRightClick()) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        String drug = getDrugName(item);
        if (drug == null) return;

        event.setCancelled(true);

        // cooldown check
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long cooldownMs = Main.settingsManager.getUseCooldown(drug) * 1000L;

        cooldowns.putIfAbsent(uuid, new HashMap<>());
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns.containsKey(drug)) {
            long elapsed = now - playerCooldowns.get(drug);
            if (elapsed < cooldownMs) {
                long remaining = (cooldownMs - elapsed) / 1000 + 1;
                player.sendMessage("§cWait §f" + remaining + "s §cbefore using §f" + drug + "§c again.");
                return;
            }
        }

        playerCooldowns.put(drug, now);

        // consume item
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }

        Main.addictionManager.addScore(player.getUniqueId(), drug,
                Main.settingsManager.getAddictionPerUse(drug));
        double scoreAfter = Main.addictionManager.getScore(player.getUniqueId(), drug);

        EffectEngine.applyPositives(player, drug, scoreAfter);
        EffectEngine.sendDrugMessages(player, drug, scoreAfter);

        Particle particle = Particle.valueOf(Main.settingsManager.getParticleType(drug));
        int count = Main.settingsManager.getParticleCount(drug);
        double spread = Main.settingsManager.getParticleSpread(drug);
        double speed = Main.settingsManager.getParticleSpeed(drug);
        player.spawnParticle(particle, player.getLocation().add(0, 1, 0), count, spread, spread, spread, speed);
    }

    private String getDrugName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        if (!item.getItemMeta().hasLore()) return null;
        return item.getItemMeta().getLore().stream()
                .filter(line -> line.contains("akitosdrugs:"))
                .map(line -> line.replace("§8akitosdrugs:", "").trim())
                .findFirst()
                .orElse(null);
    }
}