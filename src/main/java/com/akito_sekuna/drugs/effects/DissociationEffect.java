package com.akito_sekuna.drugs.effects;

import com.akito_sekuna.drugs.Main;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DissociationEffect {

    private record DissociationData(
            Mannequin mannequin,
            ItemStack[] inventory,
            ItemStack[] armor,
            ItemStack offhand,
            Location origin,
            GameMode gameMode,
            BukkitTask task
    ) {}

    private static final Map<UUID, DissociationData> active = new HashMap<>();

    public static void activate(Player player, int durationTicks) {
        if (active.containsKey(player.getUniqueId())) return;

        // save state
        ItemStack[] inventory = player.getInventory().getStorageContents().clone();
        ItemStack[] armor = player.getInventory().getArmorContents().clone();
        ItemStack offhand = player.getInventory().getItemInOffHand().clone();
        Location origin = player.getLocation().clone();
        GameMode gameMode = player.getGameMode();

        // activation flash
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, false));
        player.sendActionBar("§bYou feel yourself leaving your body...");

        // delay actual dissociation by 1 second (blindness flash first)
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {

            // clear inventory
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));

            // set adventure mode + invisibility
            player.setGameMode(GameMode.ADVENTURE);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, durationTicks, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, durationTicks, 0, false, false));

            // spawn mannequin
            Mannequin mannequin = (Mannequin) player.getWorld().spawnEntity(origin, EntityType.MANNEQUIN);
            mannequin.setImmovable(true);
            mannequin.setProfile(ResolvableProfile.resolvableProfile()
                    .uuid(player.getUniqueId())
                    .name(player.getName())
                    .build());
            mannequin.getEquipment().setArmorContents(armor);
            mannequin.getEquipment().setItemInOffHand(offhand);
            mannequin.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationTicks + 100, 255, false, false));
            mannequin.setInvulnerable(true);

            // countdown task
            final int[] remaining = {durationTicks / 20};
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
                if (!player.isOnline()) {
                    deactivate(player.getUniqueId());
                    return;
                }
                if (remaining[0] <= 0 || !player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    deactivate(player.getUniqueId());
                    return;
                }
                player.sendActionBar("§bDissociated §7[" + remaining[0] + "s remaining]");
                remaining[0]--;
            }, 0L, 20L);

            active.put(player.getUniqueId(), new DissociationData(
                    mannequin, inventory, armor, offhand, origin, gameMode, task
            ));

        }, 20L);
    }

    public static void deactivate(UUID uuid) {
        DissociationData data = active.remove(uuid);
        if (data == null) return;

        data.task().cancel();

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            // snap back flash
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, false));
            player.sendActionBar("§bYou return to your body.");

            // tp to mannequin location
            player.teleport(data.mannequin().getLocation());

            // restore state
            player.setGameMode(data.gameMode());
            player.getInventory().setStorageContents(data.inventory());
            player.getInventory().setArmorContents(data.armor());
            player.getInventory().setItemInOffHand(data.offhand());

            // remove effects
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.DARKNESS);
        }

        // remove mannequin
        data.mannequin().remove();
    }

}