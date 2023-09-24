package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.CrazyCrates;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyManager;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateLocation;
import me.badbones69.crazycrates.api.objects.Prize;
import me.badbones69.crazycrates.controllers.CrateControl;
import me.badbones69.crazycrates.controllers.Preview;
import me.badbones69.crazycrates.multisupport.ServerProtocol;
import me.badbones69.crazycrates.controllers.GUIMenu;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.HashMap;

public class CCCommand implements CommandExecutor {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();
    private final FileManager fileManager = plugin.getFileManager();
    private final CrazyManager crazyManager = plugin.getCrazyManager();
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (args.length == 0) {

            if (sender instanceof Player) {
                if (!Methods.permCheck(sender, "menu")) return true;
            } else {
                sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
                return true;
            }

            GUIMenu.openGUI((Player) sender);
            return true;
        } else {
            if (args[0].equalsIgnoreCase("help")) {
                if (!Methods.permCheck(sender, "access")) return true;

                sender.sendMessage(Messages.HELP.getMessage());
                return true;
            } else if (args[0].equalsIgnoreCase("set1") || args[0].equalsIgnoreCase("set2")) {
                if (!Methods.permCheck(sender, "admin")) return true;

                if (ServerProtocol.isLegacy()) {
                    sender.sendMessage(Methods.getPrefix("&cThis command only works on 1.13+. If you wish to make schematics for 1.12.2- use World Edit to do so."));
                    return true;
                }

                Player player = (Player) sender;
                int set = args[0].equalsIgnoreCase("set1") ? 1 : 2;

                Block block = Methods.getTargetBlock(player, 10);

                if (block == null || block.isEmpty()) {
                    player.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
                    return true;
                }

                if (crazyManager.getSchematicLocations().containsKey(player.getUniqueId())) {
                    crazyManager.getSchematicLocations().put(player.getUniqueId(), new Location[] {set == 1 ? block.getLocation() : crazyManager.getSchematicLocations().getOrDefault(player.getUniqueId(), null)[0], set == 2 ? block.getLocation() : crazyManager.getSchematicLocations().getOrDefault(player.getUniqueId(), null)[1]});
                } else {
                    crazyManager.getSchematicLocations().put(player.getUniqueId(), new Location[] {set == 1 ? block.getLocation() : null, set == 2 ? block.getLocation() : null});
                }

                player.sendMessage(Methods.getPrefix("&7You have set location #" + set + "."));

                return true;
            } else if (args[0].equalsIgnoreCase("save")) { // /crates save <file name>
                if (!Methods.permCheck(sender, "admin")) return true;

                if (ServerProtocol.isLegacy()) {
                    sender.sendMessage(Methods.getPrefix("&cThis command only works on 1.13+. If you wish to make schematics for 1.12.2- use World Edit to do so."));
                    return true;
                }

                Location[] locations = crazyManager.getSchematicLocations().get(((Player) sender).getUniqueId());

                if (locations != null && locations[0] != null && locations[1] != null) {
                    if (args.length >= 2) {
                        File file = new File(plugin.getDataFolder() + "/Schematics/" + args[1]);
                        crazyManager.getNMSSupport().saveSchematic(locations, sender.getName(), file);
                        sender.sendMessage(Methods.getPrefix("&7Saved the " + args[1] + ".nbt into the Schematics folder."));
                        crazyManager.loadSchematics();
                    } else {
                        sender.sendMessage(Methods.getPrefix("&cYou need to specify a schematic file name."));
                    }
                } else {
                    sender.sendMessage(Methods.getPrefix("&cYou need to use /cc set1/set2 to set the connors of your schematic."));
                }

                return true;
            } else if (args[0].equalsIgnoreCase("additem")) { // /crates additem0 <crate>1 <prize>2
                if (!Methods.permCheck(sender, "admin")) return true;

                Player player = (Player) sender;

                if (args.length >= 3) {
                    ItemStack item = crazyManager.getNMSSupport().getItemInMainHand(player);

                    if (item != null && item.getType() != Material.AIR) {
                        Crate crate = crazyManager.getCrateFromName(args[1]);

                        if (crate != null) {
                            String prize = args[2];

                            try {
                                crate.addEditorItem(prize, item);
                            } catch (Exception e) {
                                plugin.getLogger().warning( "Failed to add a new prize to the " + crate.getName() + " crate.");
                                e.printStackTrace();
                            }

                            crazyManager.loadCrates();
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Crate%", crate.getName());
                            placeholders.put("%Prize%", prize);
                            player.sendMessage(Messages.ADDED_ITEM_WITH_EDITOR.getMessage(placeholders));
                        } else {
                            player.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                        }
                    } else {
                        player.sendMessage(Messages.NO_ITEM_IN_HAND.getMessage());
                    }
                } else {
                    player.sendMessage(Methods.getPrefix("&c/crates additem <Crate> <Prize>"));
                }

                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!Methods.permCheck(sender, "admin")) return true;

                fileManager.reloadAllFiles();
                fileManager.setup();

                if (!FileManager.Files.LOCATIONS.getFile().contains("Locations")) {
                    FileManager.Files.LOCATIONS.getFile().set("Locations.Clear", null);
                    FileManager.Files.LOCATIONS.saveFile();
                }

                if (!FileManager.Files.DATA.getFile().contains("Players")) {
                    FileManager.Files.DATA.getFile().set("Players.Clear", null);
                    FileManager.Files.DATA.saveFile();
                }

                crazyManager.loadCrates();
                sender.sendMessage(Messages.RELOAD.getMessage());

                return true;
            } else if (args[0].equalsIgnoreCase("debug")) {
                if (!Methods.permCheck(sender, "admin")) return true;

                if (args.length >= 2) {
                    Crate crate = crazyManager.getCrateFromName(args[1]);

                    if (crate != null) {
                        for (Prize prize : crate.getPrizes()) {
                            crazyManager.givePrize((Player) sender, prize);
                        }
                    } else {
                        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                        return true;
                    }

                    return true;
                }

                sender.sendMessage(Methods.getPrefix("&c/crates debug <Crate>"));
                return true;
            } else if (args[0].equalsIgnoreCase("admin")) {
                if (!(sender instanceof Player)) return true;

                Player player = (Player) sender;

                if (!Methods.permCheck(player, "admin")) return true;

                int size = crazyManager.getCrates().size();

                int slots = 9;
                for (; size > 9; size -= 9) slots += 9;

                Inventory inv = plugin.getServer().createInventory(null, slots, Methods.color("&4&lAdmin Keys"));

                for (Crate crate : crazyManager.getCrates()) {
                    if (crate.getCrateType() != CrateType.MENU) {
                        if (inv.firstEmpty() >= 0) inv.setItem(inv.firstEmpty(), crate.getAdminKey());
                    }
                }

                player.openInventory(inv);
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                if (!Methods.permCheck(sender, "admin")) return true;

                StringBuilder crates = new StringBuilder();
                String brokecrates;

                for (Crate crate : crazyManager.getCrates()) {
                    crates.append("&a").append(crate.getName()).append("&8, ");
                }

                StringBuilder brokecratesBuilder = new StringBuilder();
                for (String crate : crazyManager.getBrokeCrates()) {
                    brokecratesBuilder.append("&c").append(crate).append(".yml&8, ");
                }

                brokecrates = brokecratesBuilder.toString();
                sender.sendMessage(Methods.color("&e&lCrates:&f " + crates));

                if (brokecrates.length() > 0) sender.sendMessage(Methods.color("&6&lBroken Crates:&f " + brokecrates.substring(0, brokecrates.length() - 2)));

                sender.sendMessage(Methods.color("&e&lAll Crate Locations:"));
                sender.sendMessage(Methods.color("&c[ID]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]"));

                int line = 1;

                for (CrateLocation loc : crazyManager.getCrateLocations()) {
                    Crate crate = loc.getCrate();
                    String world = loc.getLocation().getWorld().getName();
                    int x = loc.getLocation().getBlockX();
                    int y = loc.getLocation().getBlockY();
                    int z = loc.getLocation().getBlockZ();
                    sender.sendMessage(Methods.color("&8[&b" + line + "&8]: " + "&c" + loc.getID() + "&8, &c" + crate.getName() + "&8, &c" + world + "&8, &c" + x + "&8, &c" + y + "&8, &c" + z));
                    line++;
                }

                return true;
            } else if (args[0].equalsIgnoreCase("tp")) { // /crates tp <Location>
                if (!Methods.permCheck(sender, "admin")) return true;

                if (args.length == 2) {
                    String Loc = args[1];

                    if (!FileManager.Files.LOCATIONS.getFile().contains("Locations")) {
                        FileManager.Files.LOCATIONS.getFile().set("Locations.Clear", null);
                        FileManager.Files.LOCATIONS.saveFile();
                    }

                    for (String name : FileManager.Files.LOCATIONS.getFile().getConfigurationSection("Locations").getKeys(false)) {
                        if (name.equalsIgnoreCase(Loc)) {
                            World W = plugin.getServer().getWorld(FileManager.Files.LOCATIONS.getFile().getString("Locations." + name + ".World"));
                            int X = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".X");
                            int Y = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".Y");
                            int Z = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".Z");
                            Location loc = new Location(W, X, Y, Z);
                            ((Player) sender).teleport(loc.add(.5, 0, .5));
                            sender.sendMessage(Methods.color(Methods.getPrefix() + "&7You have been teleported to &6" + name + "&7."));
                            return true;
                        }
                    }

                    sender.sendMessage(Methods.color(Methods.getPrefix() + "&cThere is no location called &6" + Loc + "&c."));
                    return true;
                }

                sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/crates tp <Location Name>"));
                return true;
            } else if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("s")) { // /crates set <Crate>
                if (!Methods.permCheck(sender, "admin")) return true;

                if (!(sender instanceof Player)) {
                    sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
                    return true;
                }

                if (args.length == 2) {
                    Player player = (Player) sender;
                    String c = args[1]; // Crate

                    for (Crate crate : crazyManager.getCrates()) {
                        if (crate.getName().equalsIgnoreCase(c)) {
                            Block block = player.getTargetBlock(null, 5);

                            if (block.isEmpty()) {
                                player.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
                                return true;
                            }

                            crazyManager.addCrateLocation(block.getLocation(), crate);
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Crate%", crate.getName());
                            placeholders.put("%Prefix%", Methods.getPrefix());
                            player.sendMessage(Messages.CREATED_PHYSICAL_CRATE.getMessage(placeholders));
                            return true;
                        }
                    }

                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", c));
                    return true;
                }

                sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/crates Set <Crate>"));
                return true;
            } else if (args[0].equalsIgnoreCase("preview")) { // /crates preview <Crate> [Player]
                if (sender instanceof Player) {
                    if (!Methods.permCheck(sender, "preview")) return true;
                }

                if (args.length >= 2) {
                    Crate crate = null;
                    Player player;

                    for (Crate c : crazyManager.getCrates()) {
                        if (c.getCrateType() != CrateType.MENU) {
                            if (c.getName().equalsIgnoreCase(args[1])) crate = c;
                        }
                    }

                    if (crate != null) {
                        if (crate.isPreviewEnabled()) {
                            if (crate.getCrateType() != CrateType.MENU) {
                                if (args.length >= 3) {
                                    if (Methods.isOnline(args[2], sender)) {
                                        player = Methods.getPlayer(args[2]);
                                    } else {
                                        return true;
                                    }
                                } else {
                                    if (!(sender instanceof Player)) {
                                        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/crates preview <Crate> [Player]"));
                                        return true;
                                    } else {
                                        player = (Player) sender;
                                    }
                                }

                                Preview.setPlayerInMenu(player, false);
                                Preview.openNewPreview(player, crate);
                            }
                        } else {
                            sender.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
                        }

                        return true;
                    }
                }

                sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/crates preview <Crate> [Player]"));
                return true;
            } else if (args[0].equalsIgnoreCase("open")) { // /crates open <Crate> [Player]
                if (!Methods.permCheck(sender, "open")) return true;

                if (args.length >= 2) {
                    for (Crate crate : crazyManager.getCrates()) {
                        if (crate.getName().equalsIgnoreCase(args[1])) {
                            Player player;
                            if (args.length >= 3) {
                                if (!Methods.permCheck(sender, "open.other")) return true;

                                if (Methods.isOnline(args[2], sender)) {
                                    player = Methods.getPlayer(args[2]);
                                } else {
                                    return true;
                                }
                            } else {
                                if (!(sender instanceof Player)) {
                                    sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/crates open <Crate> [Player]"));
                                    return true;
                                } else {
                                    player = (Player) sender;
                                }
                            }

                            if (crazyManager.isInOpeningList(player)) {
                                sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                                return true;
                            }

                            CrateType type = crate.getCrateType();

                            if (type != null) {
                                FileConfiguration config = FileManager.Files.CONFIG.getFile();
                                boolean hasKey = false;
                                KeyType keyType = KeyType.VIRTUAL_KEY;

                                if (crazyManager.getVirtualKeys(player, crate) >= 1) {
                                    hasKey = true;
                                } else {
                                    if (config.getBoolean("Settings.Virtual-Accepts-Physical-Keys")) {
                                        if (crazyManager.hasPhysicalKey(player, crate, false)) {
                                            hasKey = true;
                                            keyType = KeyType.PHYSICAL_KEY;
                                        }
                                    }
                                }

                                if (!hasKey) {
                                    if (config.contains("Settings.Need-Key-Sound")) {
                                        Sound sound = Sound.valueOf(config.getString("Settings.Need-Key-Sound"));
                                        if (sound != null) player.playSound(player.getLocation(), sound, 1f, 1f);
                                    }

                                    player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
                                    CrateControl.knockBack(player, player.getTargetBlock(null, 1).getLocation().add(.5, 0, .5));
                                    return true;
                                }

                                if (Methods.isInventoryFull(player)) {
                                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                    return true;
                                }

                                if (type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER && type != CrateType.QUAD_CRATE) {
                                    crazyManager.openCrate(player, crate, keyType, player.getLocation(), true, false);
                                    HashMap<String, String> placeholders = new HashMap<>();
                                    placeholders.put("%Crate%", crate.getName());
                                    placeholders.put("%Player%", player.getName());
                                    sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));
                                } else {
                                    sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                                }
                            } else {
                                sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                            }

                            return true;
                        }
                    }
                }

                sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/crates open <Crate> [Player]"));
                return true;
            } else if (args[0].equalsIgnoreCase("forceopen") || args[0].equalsIgnoreCase("fo") || args[0].equalsIgnoreCase("fopen")) { // /cc ForceOpen <Crate> [Player]
                if (!Methods.permCheck(sender, "forceopen")) return true;

                if (args.length >= 2) {
                    for (Crate crate : crazyManager.getCrates()) {
                        if (crate.getCrateType() != CrateType.MENU) {
                            if (crate.getName().equalsIgnoreCase(args[1])) {
                                Player player;

                                if (args.length >= 3) {
                                    if (Methods.isOnline(args[2], sender)) {
                                        player = Methods.getPlayer(args[2]);
                                    } else {
                                        return true;
                                    }
                                } else {
                                    if (!(sender instanceof Player)) {
                                        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/crates forceopen <Crate> [Player]"));
                                        return true;
                                    } else {
                                        player = (Player) sender;
                                    }
                                }

                                if (crazyManager.isInOpeningList(player)) {
                                    sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                                    return true;
                                }

                                CrateType type = crate.getCrateType();

                                if (type != null) {
                                    if (type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER) {
                                        crazyManager.openCrate(player, crate, KeyType.FREE_KEY, player.getLocation(), true, false);
                                        HashMap<String, String> placeholders = new HashMap<>();
                                        placeholders.put("%Crate%", crate.getName());
                                        placeholders.put("%Player%", player.getName());
                                        sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));
                                    } else {
                                        sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                                    }
                                } else {
                                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                                }

                                return true;
                            }
                        }
                    }

                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                    return true;
                }

                sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/crates forceopen <Crate> [Player]"));
                return true;
            } else if (args[0].equalsIgnoreCase("transfer") || args[0].equalsIgnoreCase("tran")) { // /crates transfer <crate> <player> [amount]
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
                    return true;
                }

                if (Methods.permCheck(sender, "transfer")) {
                    if (args.length >= 3) {
                        Crate crate = crazyManager.getCrateFromName(args[1]);

                        if (crate != null) {
                            if (!args[2].equalsIgnoreCase(sender.getName())) {
                                Player target;
                                Player player = (Player) sender;

                                if (Methods.isOnline(args[2], sender)) {
                                    target = Methods.getPlayer(args[2]);
                                } else {
                                    sender.sendMessage(Messages.NOT_ONLINE.getMessage("%Player%", args[2]));
                                    return true;
                                }

                                int amount = 1;

                                if (args.length >= 4) {
                                    if (!Methods.isInt(args[3])) {
                                        sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                                        return true;
                                    }

                                    amount = Integer.parseInt(args[3]);
                                }

                                if (crazyManager.getVirtualKeys(player, crate) >= amount) {
                                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReciveReason.TRANSFER, amount);
                                    plugin.getServer().getPluginManager().callEvent(event);

                                    if (!event.isCancelled()) {
                                        int finalAmount = amount;
                                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                            crazyManager.takeKeys(finalAmount, player, crate);
                                            crazyManager.addKeys(finalAmount, target, crate);
                                        });
                                        HashMap<String, String> placeholders = new HashMap<>();
                                        placeholders.put("%Crate%", crate.getName());
                                        placeholders.put("%Amount%", amount + "");
                                        placeholders.put("%Player%", target.getName());
                                        player.sendMessage(Messages.TRANSFERRED_KEYS.getMessage(placeholders));
                                        placeholders.put("%Player%", player.getName());
                                        target.sendMessage(Messages.RECEIVED_TRANSFERRED_KEYS.getMessage(placeholders));
                                    }
                                } else {
                                    sender.sendMessage(Messages.NOT_ENOUGH_KEYS.getMessage("%Crate%", crate.getName()));
                                }
                            } else {
                                sender.sendMessage(Messages.SAME_PLAYER.getMessage());
                            }
                        } else {
                            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                        }
                    } else {
                        sender.sendMessage(Methods.getPrefix("&c/crates transfer <Crate> <Player> [Amount]"));
                    }
                }

                return true;
            } else if (args[0].equalsIgnoreCase("giveall")) { // /crates giveall <Physical/Virtual> <Crate> [Amount]
                if (!Methods.permCheck(sender, "admin")) return true;
// /crates arg0 arg1 arg2 arg3 arg4
                if (args.length >= 3) {
                    int amount = 1;

                    if (args.length >= 4) {
                        if (!Methods.isInt(args[3])) {
                            sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                            return true;
                        }

                        amount = Integer.parseInt(args[3]);
                    }

                    KeyType type = KeyType.getFromName(args[1]);

                    if (type == null || type == KeyType.FREE_KEY) {
                        sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
                        return true;
                    }

                    Crate crate = crazyManager.getCrateFromName(args[2]);

                    if (crate != null) {
                        if (crate.getCrateType() != CrateType.MENU) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Amount%", amount + "");
                            placeholders.put("%Key%", crate.getKey().getItemMeta().getDisplayName());
                            sender.sendMessage(Messages.GIVEN_EVERYONE_KEYS.getMessage(placeholders));

                            for (Player player : plugin.getServer().getOnlinePlayers()) {
                                PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReciveReason.GIVE_ALL_COMMAND, amount);
                                plugin.getServer().getPluginManager().callEvent(event);

                                if (!event.isCancelled()) {

                                    player.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));

                                    if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
                                        player.getInventory().addItem(crate.getKey(amount));
                                        return true;
                                    }

                                    int finalAmount = amount;
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> crazyManager.addKeys(finalAmount, player, crate));
                                }
                            }

                            return true;
                        }
                    }

                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
                    return true;
                }

                sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/crates giveall <Physical/Virtual> <Crate> <Amount>"));
                return true;
            } else if (args[0].equalsIgnoreCase("give")) { // /crates give <Physical/Virtual> <Crate> [Amount] [Player]
                if (!Methods.permCheck(sender, "admin")) return true;

                Player target;
                KeyType type = KeyType.PHYSICAL_KEY;
                Crate crate = null;
                int amount = 1;

                if (args.length >= 2) {
                    type = KeyType.getFromName(args[1]);
                    if (type == null || type == KeyType.FREE_KEY) {
                        sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
                        return true;
                    }
                }

                if (args.length >= 3) {
                    crate = crazyManager.getCrateFromName(args[2]);
                    if (crate == null) {
                        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
                        return true;
                    }
                }

                if (args.length >= 4) {
                    if (!Methods.isInt(args[3])) {
                        sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                        return true;
                    }

                    amount = Integer.parseInt(args[3]);
                }

                if (args.length >= 5) {
                    target = Methods.getPlayer(args[4]);
                } else {
                    if (sender instanceof ConsoleCommandSender) {
                        sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
                        return true;
                    } else {
                        target = (Player) sender;
                    }
                }

                if (args.length >= 3) {
                    if (crate.getCrateType() != CrateType.MENU) {
                        PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(target, crate, PlayerReceiveKeyEvent.KeyReciveReason.GIVE_COMMAND, amount);
                        plugin.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {

                            if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
                                target.getInventory().addItem(crate.getKey(amount));
                            } else {
                                if (target != null) {
                                    crazyManager.addKeys(amount, target, crate);
                                } else {
                                    sender.sendMessage("We couldn't find that player.");
                                    return false;
                                }
                            }

                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Amount%", amount + "");
                            placeholders.put("%Player%", target.getName());
                            placeholders.put("%Key%", crate.getKey().getItemMeta().getDisplayName());
                            sender.sendMessage(Messages.GIVEN_A_PLAYER_KEYS.getMessage(placeholders));

                            if (target != null) target.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));
                        }

                        return true;
                    }

                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
                    return true;
                }

                sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/crates give <Physical/Virtual> <Crate> [Amount] [Player]"));
                return true;
            } else if (args[0].equalsIgnoreCase("take")) { // /crates give <Physical/Virtual> <Crate> [Amount] [Player]
                if (!Methods.permCheck(sender, "admin")) return true;
                KeyType keyType = null;

                if (args.length >= 2) keyType = KeyType.getFromName(args[1]);

                if (keyType == null || keyType == KeyType.FREE_KEY) {
                    sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
                    return true;
                }

                if (args.length == 3) {
                    Crate crate = crazyManager.getCrateFromName(args[2]);
                    if (crate != null) {
                        if (crate.getCrateType() != CrateType.MENU) {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
                                return true;
                            }

                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Amount%", "1");
                            placeholders.put("%Player%", sender.getName());
                            sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));

                            //if (!crazyManager.takeKeys(1, (Player) sender, crate, keyType, false)) Methods.failedToTakeKey((Player) sender, crate);
                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                boolean took = crazyManager.takeKeys(1, (Player) sender, crate);
                                if (!took) Bukkit.getScheduler().runTask(plugin, () -> Methods.failedToTakeKey((Player) sender, crate));
                            });

                            return true;
                        }
                    }
                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
                    return true;
                } else if (args.length == 4) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
                        return true;
                    }

                    if (!Methods.isInt(args[3])) {
                        sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                        return true;
                    }

                    int amount = Integer.parseInt(args[3]);
                    Crate crate = crazyManager.getCrateFromName(args[2]);

                    if (crate != null) {
                        if (crate.getCrateType() != CrateType.MENU) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Amount%", amount + "");
                            placeholders.put("%Player%", sender.getName());
                            sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));

                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                               boolean took = crazyManager.takeKeys(amount, (Player) sender, crate);
                               if (!took) Bukkit.getScheduler().runTask(plugin, () -> Methods.failedToTakeKey((Player) sender, crate));
                            });

                            return true;
                        }
                    }

                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
                    return true;
                } else if (args.length == 5) {
                    if (!Methods.isInt(args[3])) {
                        sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                        return true;
                    }

                    int amount = Integer.parseInt(args[3]);
                    Player target = Methods.getPlayer(args[4]);
                    Crate crate = crazyManager.getCrateFromName(args[2]);
                    if (crate != null) {
                        if (crate.getCrateType() != CrateType.MENU) {
                            if (keyType == KeyType.VIRTUAL_KEY) {
                                if (target != null) {
                                    HashMap<String, String> placeholders = new HashMap<>();
                                    placeholders.put("%Amount%", amount + "");
                                    placeholders.put("%Player%", target.getName());
                                    sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));

                                    //if (!crazyManager.takeKeys(amount, target, crate, KeyType.VIRTUAL_KEY, false)) Methods.failedToTakeKey((Player) sender, crate);
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                        boolean took = crazyManager.takeKeys(amount, target, crate);
                                        if (!took) Bukkit.getScheduler().runTask(plugin, () -> Methods.failedToTakeKey((Player) sender, crate));
                                    });
                                } else {
                                    sender.sendMessage("We dont support offline players yet.");
                                    return false;
                                }
                            } else if (keyType == KeyType.PHYSICAL_KEY) {
                                if (target != null) {
                                    HashMap<String, String> placeholders = new HashMap<>();
                                    placeholders.put("%Amount%", amount + "");
                                    placeholders.put("%Player%", target.getName());
                                    sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));

                                    //if (!crazyManager.takeKeys(amount, target, crate, KeyType.PHYSICAL_KEY, false)) Methods.failedToTakeKey((Player) sender, crate);
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                        boolean took = crazyManager.takeKeys(amount, target, crate);
                                        if (!took) Bukkit.getScheduler().runTask(plugin, () -> Methods.failedToTakeKey((Player) sender, crate));
                                    });
                                } else {
                                    sender.sendMessage(Messages.NOT_ONLINE.getMessage("%Player%", args[4]));
                                }
                            }

                            return true;
                        }
                    }

                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
                    return true;
                }

                sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/crates take <Physical/Virtual> <Crate> [Amount] [Player]"));
                return true;
            }
        }

        sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease do /crates help for more info."));
        return true;
    }
}
