package dev.danzel.smash.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private Map<String, GamePlayer> players;

    public PlayerManager() {
        players = new HashMap<>();
        Bukkit.getOnlinePlayers().forEach(player -> players.put(player.getName(), new GamePlayer(player)));
    }

    public GamePlayer getPlayer(String name) {
        return players.get(name);
    }

    public void removePlayer(String name) {
        players.remove(name);
    }

    public void addPlayer(Player player) {
        players.put(player.getName(), new GamePlayer(player));
    }

    public Map<String, GamePlayer> getPlayers() {
        return players;
    }
}
