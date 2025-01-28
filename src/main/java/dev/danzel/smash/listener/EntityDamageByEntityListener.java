package dev.danzel.smash.listener;

import dev.danzel.smash.Smash;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageByEntityListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && event.getEntity() instanceof Player player) {
            event.setCancelled(true);
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                Smash.getInstance().getPlayerManager().getPlayer(player.getName()).damage();
                player.setVelocity(player.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().multiply(0.5));
            }
        }
    }
}
