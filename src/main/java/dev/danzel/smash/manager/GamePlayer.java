package dev.danzel.smash.manager;

import dev.danzel.smash.Smash;
import dev.danzel.smash.data.Data;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamePlayer {
    private Player player;
    private int hp;
    private int kb;
    private boolean win;

    public GamePlayer(Player player) {
        this.player = player;
        this.hp = 3;
        this.kb = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void damage() {
        int randomKb = (int) (Math.random() * 10);
        kb = kb + randomKb;
    }

    public void kill() {
        hp = hp - 1;
        if (hp == 0) {
            player.setGameMode(GameMode.SPECTATOR);
            Smash.getInstance().getGameManager().setGameState(GameManager.GameState.RESTART);
            Smash.getInstance().getGameManager().stop();
            return;
        }

        player.teleport(Data.getRandomSpawn());
    }

    public void setPlayerName(String name) {

    }

    public void setWinner(boolean win) {
        this.win = win;
    }

    public boolean isWinner() {
        return win;
    }
}
