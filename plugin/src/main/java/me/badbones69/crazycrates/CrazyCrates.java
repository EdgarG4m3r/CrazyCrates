package me.badbones69.crazycrates;

import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.CrazyManager;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.FileManager.Files;
import me.badbones69.crazycrates.api.objects.QuadCrateSession;
import me.badbones69.crazycrates.commands.CCCommand;
import me.badbones69.crazycrates.commands.CCTab;
import me.badbones69.crazycrates.commands.KeyCommand;
import me.badbones69.crazycrates.commands.KeyTab;
import me.badbones69.crazycrates.controllers.BrokeLocationsControl;
import me.badbones69.crazycrates.controllers.CrateControl;
import me.badbones69.crazycrates.controllers.FireworkDamageEvent;
import me.badbones69.crazycrates.controllers.GUIMenu;
import me.badbones69.crazycrates.controllers.Preview;
import me.badbones69.crazycrates.cratetypes.CSGO;
import me.badbones69.crazycrates.cratetypes.Cosmic;
import me.badbones69.crazycrates.cratetypes.CrateOnTheGo;
import me.badbones69.crazycrates.cratetypes.QuadCrate;
import me.badbones69.crazycrates.cratetypes.QuickCrate;
import me.badbones69.crazycrates.cratetypes.Roulette;
import me.badbones69.crazycrates.cratetypes.War;
import me.badbones69.crazycrates.cratetypes.Wheel;
import me.badbones69.crazycrates.cratetypes.Wonder;
import me.badbones69.crazycrates.multisupport.Events_v1_11_R1_Down;
import me.badbones69.crazycrates.multisupport.Events_v1_12_R1_Up;
import me.badbones69.crazycrates.multisupport.ServerProtocol;
import me.badbones69.crazycrates.multisupport.Support;
import me.badbones69.crazycrates.multisupport.placeholders.MVdWPlaceholderAPISupport;
import me.badbones69.crazycrates.multisupport.placeholders.PlaceholderAPISupport;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyCrates extends JavaPlugin implements Listener {

    private final CrazyManager crazyManager = CrazyManager.getInstance();

    private final FileManager fileManager = CrazyManager.getFileManager();

    private boolean isEnabled = true; // If the server is supported

    private final JavaPlugin plugin = this;
    
    @Override
    public void onEnable() {

        // Initialize the plugin variable.
        crazyManager.loadPlugin(this);

        // Crate Files
        String extensions = ServerProtocol.getCurrentProtocol().isNewer(ServerProtocol.v1_12_R1) ? "nbt" : "schematic";
        String cratesFolder = ServerProtocol.getCurrentProtocol().isNewer(ServerProtocol.v1_12_R1) ? "/Crates1.13-Up" : "/Crates1.12.2-Down";
        String schemFolder = ServerProtocol.getCurrentProtocol().isNewer(ServerProtocol.v1_12_R1) ? "/Schematics1.13-Up" : "/Schematics1.12.2-Down";

        fileManager.logInfo(true)
                .registerDefaultGenerateFiles("Basic.yml", "/Crates", cratesFolder)
                .registerDefaultGenerateFiles("Classic.yml", "/Crates", cratesFolder)
                .registerDefaultGenerateFiles("Crazy.yml", "/Crates", cratesFolder)
                .registerDefaultGenerateFiles("Galactic.yml", "/Crates", cratesFolder)
                //Schematics
                .registerDefaultGenerateFiles("classic." + extensions, "/Schematics", schemFolder)
                .registerDefaultGenerateFiles("nether." + extensions, "/Schematics", schemFolder)
                .registerDefaultGenerateFiles("outdoors." + extensions, "/Schematics", schemFolder)
                .registerDefaultGenerateFiles("sea." + extensions, "/Schematics", schemFolder)
                .registerDefaultGenerateFiles("soul." + extensions, "/Schematics", schemFolder)
                .registerDefaultGenerateFiles("wooden." + extensions, "/Schematics", schemFolder)
                //Register all files inside the custom folders.
                .registerCustomFilesFolder("/Crates")
                .registerCustomFilesFolder("/Schematics")
                .setup(this);

        if (!Files.LOCATIONS.getFile().contains("Locations")) {
            Files.LOCATIONS.getFile().set("Locations.Clear", null);
            Files.LOCATIONS.saveFile();
        }

        if (!Files.DATA.getFile().contains("Players")) {
            Files.DATA.getFile().set("Players.Clear", null);
            Files.DATA.saveFile();
        }

        Messages.addMissingMessages();

        crazyManager.loadCrates();

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(this, plugin);
        pluginManager.registerEvents(new GUIMenu(), plugin);
        pluginManager.registerEvents(new Preview(), plugin);
        pluginManager.registerEvents(new QuadCrate(), plugin);
        pluginManager.registerEvents(new War(), plugin);
        pluginManager.registerEvents(new CSGO(), plugin);
        pluginManager.registerEvents(new Wheel(), plugin);
        pluginManager.registerEvents(new Wonder(), plugin);
        pluginManager.registerEvents(new Cosmic(), plugin);
        pluginManager.registerEvents(new Roulette(), plugin);
        pluginManager.registerEvents(new QuickCrate(), plugin);
        pluginManager.registerEvents(new CrateControl(), plugin);
        pluginManager.registerEvents(new CrateOnTheGo(), plugin);

        if (ServerProtocol.isAtLeast(ServerProtocol.v1_12_R1)) {
            pluginManager.registerEvents(new Events_v1_12_R1_Up(), plugin);
        } else {
            pluginManager.registerEvents(new Events_v1_11_R1_Down(), plugin);
        }

        if (!crazyManager.getBrokeCrateLocations().isEmpty()) {
            pluginManager.registerEvents(new BrokeLocationsControl(), plugin);
        }

        pluginManager.registerEvents(new FireworkDamageEvent(), plugin);

        if (Support.PLACEHOLDERAPI.isPluginLoaded()) {
            new PlaceholderAPISupport().register();
        }

        if (Support.MVDWPLACEHOLDERAPI.isPluginLoaded()) {
            MVdWPlaceholderAPISupport.registerPlaceholders(plugin);
        }

        FileConfiguration config = Files.CONFIG.getFile();

        boolean metricsEnabled = config.getBoolean("Settings.Toggle-Metrics");
        String metricsPath = config.getString("Settings.Toggle-Metrics");

        if (metricsPath == null) {
            config.set("Settings.Toggle-Metrics", true);

            Files.CONFIG.saveFile();
        }

        if (metricsEnabled) new Metrics(this, 4514);

        registerCommand(getCommand("key"), new KeyTab(), new KeyCommand());
        registerCommand(getCommand("crazycrates"), new CCTab(), new CCCommand());
    }

    private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    @Override
    public void onDisable() {
        if (isEnabled) {
            QuadCrateSession.endAllCrates();
            QuickCrate.removeAllRewards();

            if (crazyManager.getHologramController() != null) {
                crazyManager.getHologramController().removeAllHolograms();
            }
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        crazyManager.setNewPlayerKeys(player);
        crazyManager.loadOfflinePlayersKeys(player);
    }
}
