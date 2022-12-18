package me.badbones69.crazycrates.multisupport.holograms;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyManager;
import me.badbones69.crazycrates.api.interfaces.HologramController;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateHologram;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.block.Block;
import java.util.HashMap;

public class HolographicSupport implements HologramController {
    
    private static final CrazyManager crazyManager = CrazyManager.getInstance();
    private static final HashMap<Block, Hologram> holograms = new HashMap<>();

    private static final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(crazyManager.getPlugin());
    
    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();

        if (crateHologram.isEnabled()) {
            double height = crateHologram.getHeight();
            Hologram hologram = api.createHologram(block.getLocation().add(.5, height, .5));

            for (String line : crateHologram.getMessages()) {
                hologram.getLines().appendText(Methods.color(line));
            }

            holograms.put(block, hologram);
        }
    }
    
    public void removeHologram(Block block) {
        if (!holograms.containsKey(block)) return;

        Hologram hologram = holograms.get(block);

        holograms.remove(block);
        hologram.delete();
    }
    
    public void removeAllHolograms() {
        holograms.keySet().forEach(block -> holograms.get(block).delete());
        holograms.clear();
    }
}