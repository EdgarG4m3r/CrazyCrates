package me.badbones69.crazycrates.multisupport.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.badbones69.crazycrates.CrazyCrates;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.interfaces.HologramController;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateHologram;
import org.bukkit.block.Block;
import java.util.HashMap;

public class HolographicSupport implements HologramController {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final HashMap<Block, Hologram> holograms = new HashMap<>();
    
    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();

        if (crateHologram.isEnabled()) {
            double height = crateHologram.getHeight();
            Hologram hologram = HologramsAPI.createHologram(plugin, block.getLocation().add(.5, height, .5));

            crateHologram.getMessages().stream().map(Methods::color).forEach(hologram::appendTextLine);

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