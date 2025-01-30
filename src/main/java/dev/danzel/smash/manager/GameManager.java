package dev.danzel.smash.manager;

import dev.danzel.smash.Smash;
import dev.danzel.smash.data.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private GameState gameState;
    private List<Player> inQueue;

    public GameManager() {
        gameState = GameState.LOBBY;
        inQueue = new ArrayList<>();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void checkStart() {
        System.out.println("Checking start");
        if (gameState == GameState.LOBBY && Smash.getInstance().getPlayerManager().getPlayers().size() >= 2) {
            //check if enough players are in queue area
            if (inQueue.size() < 2) return;

            System.out.println("Starting game");

            final int[] cooldown = {10};
            new BukkitRunnable() {
                @Override
                public void run() {
                    //check if queue is under 2 than cancel and set cooldown to 10
                    if (inQueue.size() < 2) {
                        cooldown[0] = 10;
                        cancel();
                        return;
                    }

                    if (cooldown[0] <= 0) {
                        start();
                        System.out.println("TELEPORT...");
                        cancel();
                        return;
                    }

                    Title.Times times = Title.Times.times(Duration.ofSeconds(1), Duration.ofMinutes(1), Duration.ofSeconds(1));

                    Title title = Title.title(
                            Component.text("Das Spiel startet in " + cooldown[0])
                                    .color(NamedTextColor.GREEN),
                            Component.text(""),
                            times);

                    cooldown[0]--;
                    Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(
                            title
                    ));
                }
            }.runTaskTimer(Smash.getInstance(), 0, 20);
        }
    }

    public void start() {
        if (gameState != GameState.LOBBY) return;
        setGameState(GameState.INGAME);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.teleport(Data.getRandomSpawn());
            onlinePlayer.showTitle(
                    Title.title(
                            Component.text("Das Spiel beginnt!")
                                    .color(NamedTextColor.GREEN)
                                    .decorate(TextDecoration.BOLD),
                            Component.text("")));
        }
    }

    public void stop() {
        if (gameState != GameState.INGAME) return;
        //todo
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (GamePlayer gamePlayer : Smash.getInstance().getPlayerManager().getPlayers().values()) {
                String subtitle = gamePlayer.isWinner() ? "Du hast gewonnen!" : "Du hast verloren!";
                onlinePlayer.showTitle(
                        Title.title(
                                Component.text("Das Spiel ist vorbei!")
                                        .color(NamedTextColor.RED)
                                        .decorate(TextDecoration.BOLD),
                                Component.text(subtitle).color(NamedTextColor.GOLD))
                );
                gamePlayer.getPlayer().teleport(Data.getQueue().getWorld().getSpawnLocation());
            }
        }
    }

    public void addPlayerToQueue(Player player) {
        inQueue.add(player);
    }

    public void removePlayerFromQueue(Player player) {
        inQueue.remove(player);
    }

    public boolean isInQueue(Player player) {
        return inQueue.contains(player);
    }

    public enum GameState {
        LOBBY, INGAME, RESTART
    }
}
