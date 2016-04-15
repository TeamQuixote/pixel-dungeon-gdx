package com.teamquixote.ai.heuristic;

import com.teamquixote.ai.heuristic.actions.Action;

public abstract class AiAgent {
    protected abstract Action makeDecision(GameState state);
}