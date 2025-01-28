package dev.danzel.smash.listener;

import dev.danzel.smash.Smash;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo().getY() <= 0) {
            Smash.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName()).kill();
        }
    }

}
