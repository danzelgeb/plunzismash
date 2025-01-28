package dev.danzel.smash.manager;

import dev.danzel.smash.Smash;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager {
    private GameState gameState;

    public GameManager() {
        gameState = GameState.LOBBY;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void checkStart() {
        if (gameState == GameState.LOBBY && Smash.getInstance().getPlayerManager().getPlayers().size() >= 2) {
            setGameState(GameState.INGAME);
            //todo start cooldown && random tp
            final int[] cooldown = {10};
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (cooldown[0] == 0) {
                        //todo start game
                        cancel();
                    }
                    cooldown[0]--;
                    Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(
                            Title.title(Component.text("Das Spiel startet in " + cooldown[0]), Component.text(""))
                    ));
                }
            }.run();
        }
    }

    public enum GameState {
        LOBBY, INGAME, RESTART
    }
}
