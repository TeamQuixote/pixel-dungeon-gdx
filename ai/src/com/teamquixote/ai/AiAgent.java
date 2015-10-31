package com.teamquixote.ai;

import com.teamquixote.ai.actions.Action;

public abstract class AiAgent {
    protected abstract Action makeDecision(GameState state);
}