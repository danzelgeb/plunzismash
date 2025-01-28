package dev.danzel.smash.manager;

import dev.danzel.smash.data.Data;
import org.bukkit.entity.Player;

public class GamePlayer {
    private Player player;
    private int hp;
    private int kb;

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
            player.kickPlayer("GAME OVER");
            //TODO end game
            return;
        }

        player.teleport(Data.getRandomSpawn());
    }
}
