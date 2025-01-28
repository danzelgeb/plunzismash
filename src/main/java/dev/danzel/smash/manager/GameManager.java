package dev.danzel.smash.manager;

import dev.danzel.smash.Smash;

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
            gameState = GameState.INGAME;
            //todo start cooldown && random tp
        }
    }

    public enum GameState {
        LOBBY, INGAME, RESTART
    }
}
