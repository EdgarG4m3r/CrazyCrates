package me.badbones69.crazycrates.controllers;

import me.badbones69.crazycrates.CrazyCrates;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyManager;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GUIMenu implements Listener {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();
    private static final CrazyManager crazyManager = plugin.getCrazyManager();
    
    public static void openGUI(Player player) {
        int size = FileManager.Files.CONFIG.getFile().getInt("Settings.InventorySize");

        Inventory inv = plugin.getServer().createInventory(null, size, Methods.sanitizeColor(FileManager.Files.CONFIG.getFile().getString("Settings.InventoryName")));

        if (FileManager.Files.CONFIG.getFile().contains("Settings.Filler.Toggle")) {
            if (FileManager.Files.CONFIG.getFile().getBoolean("Settings.Filler.Toggle")) {
                String id = FileManager.Files.CONFIG.getFile().getString("Settings.Filler.Item");
                String name = FileManager.Files.CONFIG.getFile().getString("Settings.Filler.Name");
                List<String> lore = FileManager.Files.CONFIG.getFile().getStringList("Settings.Filler.Lore");
                ItemStack item = new ItemBuilder().setMaterial(id).setName(name).setLore(lore).build();

                for (int i = 0; i < size; i++) {
                    inv.setItem(i, item.clone());
                }
            }
        }

        if (FileManager.Files.CONFIG.getFile().contains("Settings.GUI-Customizer")) {
            for (String custom : FileManager.Files.CONFIG.getFile().getStringList("Settings.GUI-Customizer")) {
                int slot = 0;
                ItemBuilder item = new ItemBuilder();
                String[] split = custom.split(", ");

                for (String option : split) {

                    if (option.contains("Item:")) {
                        item.setMaterial(option.replace("Item:", ""));
                    }

                    if (option.contains("Name:")) {
                        option = option.replace("Name:", "");

                        for (Crate crate : crazyManager.getCrates()) {
                            if (crate.getCrateType() != CrateType.MENU) {
                                option = option.replaceAll("%" + crate.getName().toLowerCase() + "%", crazyManager.getVirtualKeys(player, crate) + "")
                                .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", crazyManager.getPhysicalKeys(player, crate) + "")
                                .replaceAll("%" + crate.getName().toLowerCase() + "_total%", crazyManager.getTotalKeys(player, crate) + "");
                            }
                        }

                        item.setName(option.replaceAll("%player%", player.getName()));
                    }

                    if (option.contains("Lore:")) {
                        option = option.replace("Lore:", "");
                        String[] d = option.split(",");
                        for (String l : d) {
                            for (Crate crate : crazyManager.getCrates()) {
                                if (crate.getCrateType() != CrateType.MENU) {
                                    option = option.replaceAll("%" + crate.getName().toLowerCase() + "%", crazyManager.getVirtualKeys(player, crate) + "")
                                    .replaceAll("%" + crate.getName().toLowerCase() + "_physical%", crazyManager.getPhysicalKeys(player, crate) + "")
                                    .replaceAll("%" + crate.getName().toLowerCase() + "_total%", crazyManager.getTotalKeys(player, crate) + "");
                                }
                            }

                            item.addLore(option.replaceAll("%player%", player.getName()));
                        }
                    }

                    if (option.contains("Glowing:")) item.setGlowing(Boolean.parseBoolean(option.replace("Glowing:", "")));

                    if (option.contains("Player:")) item.setPlayer(option.replaceAll("%player%", player.getName()));

                    if (option.contains("Slot:")) slot = Integer.parseInt(option.replace("Slot:", ""));

                    if (option.contains("Unbreakable-Item")) item.setUnbreakable(Boolean.parseBoolean(option.replace("Unbreakable-Item:", "")));

                    if (option.contains("Hide-Item-Flags")) item.hideItemFlags(Boolean.parseBoolean(option.replace("Hide-Item-Flags:", "")));
                }

                if (slot > size) continue;

                slot--;
                inv.setItem(slot, item.build());
            }
        }

        for (Crate crate : crazyManager.getCrates()) {
            FileConfiguration file = crate.getFile();

            if (file != null) {
                if (file.getBoolean("Crate.InGUI")) {
                    String path = "Crate.";
                    int slot = file.getInt(path + "Slot");

                    if (slot > size) continue;

                    slot--;
                    inv.setItem(slot, new ItemBuilder()
                    .setMaterial(file.getString(path + "Item"))
                    .setName(file.getString(path + "Name"))
                    .setLore(file.getStringList(path + "Lore"))
                    .setCrateName(crate.getName())
                    .setPlayer(file.getString(path + "Player"))
                    .setGlowing(file.getBoolean(path + "Glowing"))
                    .addLorePlaceholder("%Keys%", NumberFormat.getNumberInstance().format(crazyManager.getVirtualKeys(player, crate)))
                    .addLorePlaceholder("%Keys_Physical%", NumberFormat.getNumberInstance().format(crazyManager.getPhysicalKeys(player, crate)))
                    .addLorePlaceholder("%Keys_Total%", NumberFormat.getNumberInstance().format(crazyManager.getTotalKeys(player, crate)))
                    .addLorePlaceholder("%Player%", player.getName())
                    .build());
                }
            }
        }

        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        FileConfiguration config = FileManager.Files.CONFIG.getFile();

        if (inv != null) {
            for (Crate crate : crazyManager.getCrates()) {
                if (crate.getCrateType() != CrateType.MENU && crate.isCrateMenu(e.getView())) return;
            }

            if (e.getView().getTitle().equals(Methods.sanitizeColor(config.getString("Settings.InventoryName")))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    ItemStack item = e.getCurrentItem();
                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        NBTItem nbtItem = new NBTItem(item);
                        if (nbtItem.hasNBTData() && nbtItem.hasKey("CrazyCrates-Crate")) {
                            Crate crate = crazyManager.getCrateFromName(nbtItem.getString("CrazyCrates-Crate"));
                            if (crate != null) {

                                if (e.getAction() == InventoryAction.PICKUP_HALF) { // Right-clicked the item
                                    if (crate.isPreviewEnabled()) {
                                        player.closeInventory();
                                        Preview.setPlayerInMenu(player, true);
                                        Preview.openNewPreview(player, crate);
                                    } else {
                                        player.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
                                    }

                                    return;
                                }

                                if (crazyManager.isInOpeningList(player)) {
                                    player.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                                    return;
                                }

                                boolean hasKey = false;
                                KeyType keyType = KeyType.VIRTUAL_KEY;
                                if (crazyManager.getVirtualKeys(player, crate) >= 1) {
                                    hasKey = true;
                                } else {
                                    if (FileManager.Files.CONFIG.getFile().getBoolean("Settings.Virtual-Accepts-Physical-Keys") && crazyManager.hasPhysicalKey(player, crate, false)) {
                                        hasKey = true;
                                        keyType = KeyType.PHYSICAL_KEY;
                                    }
                                }

                                if (!hasKey) {
                                    if (config.contains("Settings.Need-Key-Sound")) {
                                        Sound sound = Sound.valueOf(config.getString("Settings.Need-Key-Sound"));
                                        if (sound != null) player.playSound(player.getLocation(), sound, 1f, 1f);
                                    }

                                    player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
                                    return;
                                }

                                for (String world : getDisabledWorlds()) {
                                    if (world.equalsIgnoreCase(player.getWorld().getName())) {
                                        player.sendMessage(Messages.WORLD_DISABLED.getMessage("%World%", player.getWorld().getName()));
                                        return;
                                    }
                                }

                                if (Methods.isInventoryFull(player)) {
                                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                    return;
                                }

                                crazyManager.openCrate(player, crate, keyType, player.getLocation(), true, false);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private ArrayList<String> getDisabledWorlds() {
        return new ArrayList<>(FileManager.Files.CONFIG.getFile().getStringList("Settings.DisabledWorlds"));
    }
}