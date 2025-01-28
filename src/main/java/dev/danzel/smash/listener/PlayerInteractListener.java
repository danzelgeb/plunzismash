package dev.danzel.smash.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material material = event.getItem().getType();
            switch (material) {
                case IRON_SHOVEL:
                    event.getPlayer().getLocation().getWorld().createExplosion(event.getPlayer().getLocation(), 4);
                    break;
            }
        }
    }
}
