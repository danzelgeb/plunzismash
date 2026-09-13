package net.plunzi.punchout.listener;

import net.plunzi.punchout.Smash;
import net.plunzi.punchout.manager.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class PlayerToggleFlightListener implements Listener {

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        if (Smash.getInstance().getGameManager().getGameState() == GameManager.GameState.INGAME) {
            Player player = event.getPlayer();
            Vector direction = player.getLocation().getDirection();
            direction.setY(1);
            player.setVelocity(direction);
        }
    }
}
