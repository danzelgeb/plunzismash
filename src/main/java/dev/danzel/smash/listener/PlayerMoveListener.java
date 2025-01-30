package dev.danzel.smash.listener;

import dev.danzel.smash.Smash;
import dev.danzel.smash.data.Data;
import dev.danzel.smash.manager.GameManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getWorld() == Data.getQueue().getWorld() && event.getTo().distance(Data.getQueue()) <= 3) {
            if (Smash.getInstance().getGameManager().isInQueue(event.getPlayer()))
                return;

            Smash.getInstance().getGameManager().addPlayerToQueue(event.getPlayer());
            event.getPlayer().sendMessage(Component.text("Du bist in der Queue!"));

            if (Smash.getInstance().getGameManager().getGameState() == GameManager.GameState.LOBBY && Bukkit.getOnlinePlayers().size() <= 2) {
                Smash.getInstance().getGameManager().checkStart();
            }

            if (Smash.getInstance().getGameManager().getGameState() == GameManager.GameState.LOBBY && Bukkit.getOnlinePlayers().size() <= 2) {
                Smash.getInstance().getGameManager().checkStart();
            }
        } else {
            if (Smash.getInstance().getGameManager().isInQueue(event.getPlayer())) {
                Smash.getInstance().getGameManager().removePlayerFromQueue(event.getPlayer());
                event.getPlayer().sendMessage(Component.text("Du bist nicht mehr in der Queue!"));
            }
        }

        if (event.getTo().getY() <= 80 && Smash.getInstance().getGameManager().getGameState() == GameManager.GameState.INGAME &&
                event.getTo().getWorld().getName().equals("game") && event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            Smash.getInstance().getPlayerManager().getPlayer(event.getPlayer().getName()).kill();
        }
    }
}
