package me.badbones69.crazycrates.multisupport;

import me.badbones69.crazycrates.CrazyCrates;
import me.badbones69.crazycrates.api.CrazyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class Events_v1_12_R1_Up implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();
    private final CrazyManager crazyManager = plugin.getCrazyManager();
    
    @EventHandler
    public void onItemPickUp(EntityPickupItemEvent e) {
        if (crazyManager.isDisplayReward(e.getItem())) {
            e.setCancelled(true);
        } else {
            if (e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();

                if (crazyManager.isInOpeningList(player)) e.setCancelled(true);
            }
        }
    }
}