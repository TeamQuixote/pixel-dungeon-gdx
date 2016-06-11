package com.teamquixote.ai.agents;

import com.teamquixote.ai.actions.Action;
import com.teamquixote.ai.io.GameStateData;

public abstract class AiAgent {
    public abstract Action makeDecision(GameStateData state);
}