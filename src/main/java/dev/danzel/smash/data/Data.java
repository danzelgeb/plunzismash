package dev.danzel.smash.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class Data {
    private static List<Location> spawns;

    public Data() {
        spawns = List.of(
                new Location(Bukkit.getWorld("game"), 0, 0, 0)
        );
    }

    public static Location getRandomSpawn() {
        return spawns.get((int) (Math.random() * spawns.size()));
    }
}
