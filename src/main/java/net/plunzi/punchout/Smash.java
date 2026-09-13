package net.plunzi.punchout;

import net.plunzi.punchout.data.Data;
import net.plunzi.punchout.listener.EntityDamageByEntityListener;
import net.plunzi.punchout.listener.EntityDamageListener;
import net.plunzi.punchout.listener.FoodLevelChangeListener;
import net.plunzi.punchout.listener.PlayerInteractListener;
import net.plunzi.punchout.listener.PlayerJoinQuitListener;
import net.plunzi.punchout.listener.PlayerMoveListener;
import net.plunzi.punchout.listener.PlayerToggleFlightListener;
import net.plunzi.punchout.manager.GameManager;
import net.plunzi.punchout.manager.PlayerManager;
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
