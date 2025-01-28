package dev.danzel.smash;

import dev.danzel.smash.data.Data;
import dev.danzel.smash.listener.EntityDamageByEntityListener;
import dev.danzel.smash.listener.EntityDamageListener;
import dev.danzel.smash.listener.FoodLevelChangeListener;
import dev.danzel.smash.listener.PlayerInteractListener;
import dev.danzel.smash.listener.PlayerJoinQuitListener;
import dev.danzel.smash.listener.PlayerMoveListener;
import dev.danzel.smash.listener.PlayerToggleFlightListener;
import dev.danzel.smash.manager.GameManager;
import dev.danzel.smash.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Smash extends JavaPlugin {
    private static Smash instance;
    private GameManager gameManager;
    private PlayerManager playerManager;

    @Override
    public void onLoad() {
        instance = this;
        gameManager = new GameManager();
    }

    @Override
    public void onEnable() {
        Data.importWorld("lobby");
        Data.importWorld("game");
        playerManager = new PlayerManager();
        registerEvent(new EntityDamageByEntityListener());
        registerEvent(new EntityDamageListener());
        registerEvent(new FoodLevelChangeListener());
        registerEvent(new PlayerInteractListener());
        registerEvent(new PlayerJoinQuitListener());
        registerEvent(new PlayerMoveListener());
        registerEvent(new PlayerToggleFlightListener());
        disableChunkGeneration(Bukkit.getWorld("lobby"));
        disableChunkGeneration(Bukkit.getWorld("game"));
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

    private void registerEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    private void disableChunkGeneration(World world) {
        if (world != null) {
            world.getPopulators().clear();
            world.setKeepSpawnInMemory(false);
        }
    }
}
