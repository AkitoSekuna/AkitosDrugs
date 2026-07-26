package com.akito_sekuna.drugs.utils;

import com.akito_sekuna.drugs.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DrugMenuListener implements Listener {

    private final Main plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_MS = 100L;

    public DrugMenuListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals("§8Drug Menu")) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        int slot = event.getRawSlot();

        if (slot == 46) { DrugMenu.switchTab(player, "legal", plugin); return; }
        if (slot == 49) { DrugMenu.switchTab(player, "grayzone", plugin); return; }
        if (slot == 52) { DrugMenu.switchTab(player, "illegal", plugin); return; }
        if (slot == 8) { DrugMenu.cycleSort(player, plugin); return; }

        if (clicked.getType().name().contains("STAINED_GLASS_PANE")) return;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(uuid) && now - cooldowns.get(uuid) < COOLDOWN_MS) {
            player.sendMessage("§cWait a moment!");
            return;
        }
        cooldowns.put(uuid, now);
        player.getInventory().addItem(clicked.clone());
    }
}
