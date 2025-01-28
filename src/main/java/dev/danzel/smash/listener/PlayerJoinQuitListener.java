package dev.danzel.smash.listener;

import dev.danzel.smash.Smash;
import dev.danzel.smash.manager.GameManager;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Smash.getInstance().getGameManager().getGameState() != GameManager.GameState.LOBBY) {
            if (!event.getPlayer().isOp()) {
                event.getPlayer().kick(Component.text("The game is already in progress!"));
                return;
            }
            event.getPlayer().setGameMode(GameMode.CREATIVE);

        }
        Smash.getInstance().getPlayerManager().addPlayer(event.getPlayer());
        Smash.getInstance().getGameManager().checkStart();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Smash.getInstance().getPlayerManager().removePlayer(event.getPlayer().getName());
    }
}
