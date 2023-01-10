package me.badbones69.crazycrates.multisupport;

import me.badbones69.crazycrates.CrazyCrates;
import me.badbones69.crazycrates.api.CrazyManager;

public enum Support {
    
    PLACEHOLDERAPI("PlaceholderAPI"),
    MVDWPLACEHOLDERAPI("MVdWPlaceholderAPI"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    HOLOGRAMS("Holograms"),
    DECENT_HOLOGRAMS("DecentHolograms");
    
    private final String name;

    private final CrazyCrates plugin = CrazyCrates.getPlugin();
    
    Support(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        if (plugin.getServer().getPluginManager().getPlugin(name) != null) return plugin.isEnabled();

        return false;
    }
}