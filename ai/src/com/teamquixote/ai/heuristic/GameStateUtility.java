package com.teamquixote.ai.heuristic;

public abstract class GameStateUtility {
    public abstract boolean isTerminalState(GameState state);
    public abstract double getUtility(GameState state);
}