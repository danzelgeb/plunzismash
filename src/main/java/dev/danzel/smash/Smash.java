package dev.danzel.smash;

import dev.danzel.smash.manager.GameManager;
import dev.danzel.smash.manager.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Smash extends JavaPlugin {
    private static Smash instance;
    private GameManager gameManager;
    private PlayerManager playerManager;

    @Override
    public void onLoad() {
        instance = this;
        //todo load Map
        gameManager = new GameManager();
    }

    @Override
    public void onEnable() {
        playerManager = new PlayerManager();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static Smash getInstance() {
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
