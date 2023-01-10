package me.badbones69.crazycrates.multisupport.placeholders;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import me.badbones69.crazycrates.CrazyCrates;
import me.badbones69.crazycrates.api.CrazyManager;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.objects.Crate;
import java.text.NumberFormat;

public class MVdWPlaceholderAPISupport {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();
    private static final CrazyManager crazyManager = plugin.getCrazyManager();
    
    public static void registerPlaceholders() {
        for (final Crate crate : crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                PlaceholderAPI.registerPlaceholder(plugin, plugin.getName().toLowerCase() + "_" + crate.getName(), e -> NumberFormat.getNumberInstance().format(crazyManager.getVirtualKeys(e.getPlayer(), crate)));
                PlaceholderAPI.registerPlaceholder(plugin, plugin.getName().toLowerCase() + "_" + crate.getName() + "_physical", e -> NumberFormat.getNumberInstance().format(crazyManager.getPhysicalKeys(e.getPlayer(), crate)));
                PlaceholderAPI.registerPlaceholder(plugin, plugin.getName().toLowerCase() + "_" + crate.getName() + "_total", e -> NumberFormat.getNumberInstance().format(crazyManager.getTotalKeys(e.getPlayer(), crate)));
            }
        }
    }
}