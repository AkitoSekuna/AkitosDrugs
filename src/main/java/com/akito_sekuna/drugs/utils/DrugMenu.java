package com.akito_sekuna.drugs.utils;

import com.akito_sekuna.drugs.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class DrugMenu {

    private static final Map<UUID, String> activeTabs = new HashMap<>();
    private static final Map<UUID, String> activeSorts = new HashMap<>();

    private static final String[] SORT_MODES = {"most-recent", "a-z", "most-addictive"};

    public static void open(Player player) {
        activeTabs.put(player.getUniqueId(), "illegal");
        activeSorts.put(player.getUniqueId(), "most-recent");
        Inventory menu = Bukkit.createInventory(null, 54, "§8Drug Menu");
        fillBorder(menu);
        fillTabs(menu, "illegal");
        fillSortButton(menu, "most-recent");
        renderDrugs(menu, "illegal", "most-recent");
        player.openInventory(menu);
    }

    public static void switchTab(Player player, String tab) {
        activeTabs.put(player.getUniqueId(), tab);
        String sort = activeSorts.getOrDefault(player.getUniqueId(), "most-recent");
        Inventory menu = player.getOpenInventory().getTopInventory();
        clearDrugSlots(menu);
        fillTabs(menu, tab);
        renderDrugs(menu, tab, sort);
    }

    public static void cycleSort(Player player) {
        String current = activeSorts.getOrDefault(player.getUniqueId(), "most-recent");
        String next = SORT_MODES[(Arrays.asList(SORT_MODES).indexOf(current) + 1) % SORT_MODES.length];
        activeSorts.put(player.getUniqueId(), next);
        String tab = activeTabs.getOrDefault(player.getUniqueId(), "illegal");
        Inventory menu = player.getOpenInventory().getTopInventory();
        clearDrugSlots(menu);
        fillSortButton(menu, next);
        renderDrugs(menu, tab, next);
    }

    private static void fillBorder(Inventory menu) {
        ItemStack gray = makePane(Material.GRAY_STAINED_GLASS_PANE, "§8");
        int[] border = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,49,50,51,52,53};
        for (int slot : border) menu.setItem(slot, gray);
    }

    private static void fillTabs(Inventory menu, String activeTab) {
        menu.setItem(46, makePane(
                activeTab.equals("legal") ? Material.LIME_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE,
                "§aLegal"
        ));
        menu.setItem(49, makePane(
                activeTab.equals("grayzone") ? Material.YELLOW_STAINED_GLASS_PANE : Material.ORANGE_STAINED_GLASS_PANE,
                "§eGray-zone"
        ));
        menu.setItem(52, makePane(
                activeTab.equals("illegal") ? Material.RED_STAINED_GLASS_PANE : Material.BROWN_STAINED_GLASS_PANE,
                "§cIllegal"
        ));
    }

    private static void fillSortButton(Inventory menu, String sortMode) {
        ItemStack button = new ItemStack(Material.HOPPER);
        ItemMeta meta = button.getItemMeta();
        String label = switch (sortMode) {
            case "a-z" -> "§fSort: §bA-Z";
            case "most-addictive" -> "§fSort: §cMost Addictive";
            default -> "§fSort: §7Most Recent";
        };
        meta.setDisplayName(label);
        meta.setLore(List.of("§7Click to cycle sort mode"));
        button.setItemMeta(meta);
        menu.setItem(8, button);
    }

    private static void renderDrugs(Inventory menu, String tab, String sortMode) {
        int[] drugSlots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34};
        int index = 0;

        List<String> drugs = getSortedDrugs(tab, sortMode);

        for (String drug : drugs) {
            if (index >= drugSlots.length) break;

            ItemStack item = new ItemStack(Main.settingsManager.getItem(drug));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Main.settingsManager.getDisplayName(drug));

            List<String> lore = new ArrayList<>(Main.settingsManager.getLore(drug));
            lore.add("");
            lore.add("§8akitosdrugs:" + drug);
            meta.setLore(lore);

            item.setItemMeta(meta);
            menu.setItem(drugSlots[index++], item);
        }
    }

    private static List<String> getSortedDrugs(String tab, String sortMode) {
        List<String> drugs = Main.settingsManager.getDrugNames().stream()
                .filter(drug -> Main.settingsManager.getCategory(drug).equals(tab))
                .collect(Collectors.toList());

        switch (sortMode) {
            case "a-z" -> drugs.sort(Comparator.comparing(drug ->
                    Main.settingsManager.getDisplayName(drug).replaceAll("§.", "")));
            case "most-addictive" -> drugs.sort(Comparator.comparingDouble(
                    drug -> -Main.settingsManager.getAddictionPerUse(drug)));
            default -> Collections.reverse(drugs); // most-recent = reverse config order
        }

        return drugs;
    }

    private static void clearDrugSlots(Inventory menu) {
        int[] drugSlots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34};
        for (int slot : drugSlots) menu.setItem(slot, null);
    }

    private static ItemStack makePane(Material material, String name) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }
}