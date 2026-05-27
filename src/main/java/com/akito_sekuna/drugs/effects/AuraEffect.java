package com.akito_sekuna.drugs.effects;

import com.akito_sekuna.drugs.Main;
import com.akito_sekuna.drugs.managers.DrugEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class AuraEffect {

    public static void trigger(Player player, String drug) {
        int radius = Main.settingsManager.getAuraRadius(drug);
        List<DrugEffect> effects = Main.settingsManager.getAuraEffects(drug);

        player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).stream()
                .filter(e -> e instanceof Player && !e.equals(player))
                .map(e -> (Player) e)
                .forEach(nearby -> {
                    int displaySeconds = 0;
                    for (DrugEffect effect : effects) {
                        nearby.addPotionEffect(new PotionEffect(
                                effect.type(), effect.baseDuration(), effect.amplifier(), false, true
                        ));
                        displaySeconds = effect.baseDuration() / 20;
                    }
                    nearby.sendMessage("§dYou feel a warm wave of energy from nearby...");
                    final int seconds = displaySeconds;
                    nearby.sendActionBar("§dSomeone's energy is washing over you... §7[" + seconds + "s]");
                });
    }
}