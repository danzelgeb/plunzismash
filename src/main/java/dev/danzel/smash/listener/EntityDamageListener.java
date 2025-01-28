package dev.danzel.smash.listener;

import dev.danzel.smash.Smash;
import dev.danzel.smash.manager.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (Smash.getInstance().getGameManager().getGameState() != GameManager.GameState.INGAME) {
            event.setCancelled(true);
        }

        if (event.getEntity() instanceof Player player) {
            switch (event.getCause()) {
                case KILL:
                case VOID:
                case FALL:
                    event.setCancelled(true);
                    break;
                default:
                    break;
            }
        }
    }

}
