package com.akito_sekuna.drugs.utils;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

public class LocationUtil {

    private static final Random RANDOM = new Random();

    public static Location findSafeLocation(Location origin, int radius) {
        int attempts = 50;
        while (attempts-- > 0) {
            int dx = RANDOM.nextInt(radius * 2 + 1) - radius;
            int dz = RANDOM.nextInt(radius * 2 + 1) - radius;
            Location candidate = origin.clone().add(dx, 0, dz);

            for (int y = candidate.getBlockY() + 5; y >= candidate.getBlockY() - 5; y--) {
                Location floor = candidate.clone();
                floor.setY(y);
                Location head = floor.clone().add(0, 1, 0);
                Location body = floor.clone().add(0, 2, 0);

                if (floor.getBlock().getType().isSolid()
                        && head.getBlock().getType() == Material.AIR
                        && body.getBlock().getType() == Material.AIR) {
                    head.setPitch(origin.getPitch());
                    head.setYaw(origin.getYaw());
                    return head;
                }
            }
        }
        return null;
    }
}