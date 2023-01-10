package me.badbones69.crazycrates.multisupport;

import me.badbones69.crazycrates.CrazyCrates;
import me.badbones69.crazycrates.api.CrazyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class Events_v1_11_R1_Down implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();
    private final CrazyManager crazyManager = plugin.getCrazyManager();
    
    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent e) {
        if (crazyManager.isDisplayReward(e.getItem())) e.setCancelled(true);

        if (crazyManager.isInOpeningList(e.getPlayer())) e.setCancelled(true);
    }
}