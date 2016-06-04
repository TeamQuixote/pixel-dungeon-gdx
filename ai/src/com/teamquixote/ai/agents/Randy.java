package com.teamquixote.ai.agents;

import com.teamquixote.ai.AiAgent;
import com.teamquixote.ai.GameState;
import com.teamquixote.ai.actions.Action;

import java.util.List;
import java.util.Random;

/**
 * Randy just makes random decisions
 */
public class Randy extends AiAgent {

    private Random random = new Random();

    @Override
    protected Action makeDecision(GameState state) {
        List<Action> actions = state.getActions();
        return actions.get(random.nextInt(actions.size()));
    }
}
