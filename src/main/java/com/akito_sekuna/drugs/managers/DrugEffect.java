package com.akito_sekuna.drugs.managers;

import org.bukkit.potion.PotionEffectType;

public record DrugEffect(PotionEffectType type, int amplifier, int baseDuration) {
}