package dev.danzel.smash.data;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Data {
    private static final List<Location> spawns = List.of(
            new Location(Bukkit.getWorld("game"), 350.5, 99, 0.5),
            new Location(Bukkit.getWorld("game"), 328.5, 95, 14.5),
            new Location(Bukkit.getWorld("game"), 317.5, 95, -7.5),
            new Location(Bukkit.getWorld("game"), 361.5, 95, -22.5)
    );

    private static final List<Location> itemSpawns = List.of(
            new Location(Bukkit.getWorld("game"), 306.5, 98, -14.5),
            new Location(Bukkit.getWorld("game"), 339.5, 92, -35.5),
            new Location(Bukkit.getWorld("game"), 372.5, 95, 14.5),
            new Location(Bukkit.getWorld("game"), 339.5, 99, 35.5)
    );

    private static final List<ItemStack> items = List.of(
        ItemStack.of(Material.IRON_SHOVEL)
    );

    public static Location getQueue() {
        return new Location(Bukkit.getWorld("lobby"), -4, 55, 18);
    }

    public static Location getRandomSpawn() {
        return spawns.get((int) (Math.random() * spawns.size()));
    }

    public static Location getRandomItemSpawn() {
        return itemSpawns.get((int) (Math.random() * items.size()));
    }

    public static ItemStack getRandomItem() {
        return items.get((int) (Math.random() * items.size()));
    }

    public static void importWorld(String worldName) {
        if (Bukkit.getWorlds().parallelStream()
                .filter(world -> world.getName().equalsIgnoreCase(worldName)).count() == 1) {
            return;
        }
        Bukkit.createWorld(new WorldCreator(worldName));
    }

    public static void prepareJoin(Player player) {
        player.teleport(Bukkit.getWorld("lobby").getSpawnLocation());
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(6.0);
        player.setFoodLevel(20);
        player.getInventory().clear();
    }
}
