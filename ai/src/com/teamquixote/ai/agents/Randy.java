package com.teamquixote.ai.agents;

import com.teamquixote.ai.actions.Action;
import com.teamquixote.ai.io.GameStateData;

import java.util.List;
import java.util.Random;

/**
 * Randy just makes random decisions
 */
public class Randy extends AiAgent {

    private Random random = new Random();

    @Override
    public Action makeDecision(GameStateData state) {
        List<Action> actions = Action.getValidActions(state);
        return actions.get(random.nextInt(actions.size()));
    }
}
