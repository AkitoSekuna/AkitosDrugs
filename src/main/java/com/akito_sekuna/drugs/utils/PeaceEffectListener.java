package com.akito_sekuna.drugs.utils;

import com.akito_sekuna.drugs.effects.PeaceEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PeaceEffectListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim) {
            if (PeaceEffect.isActive(victim.getUniqueId())) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getDamager() instanceof Player attacker) {
            if (PeaceEffect.isActive(attacker.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}