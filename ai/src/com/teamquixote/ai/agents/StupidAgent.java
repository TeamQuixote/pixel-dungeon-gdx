package com.teamquixote.ai.agents;

import com.teamquixote.ai.AiAgent;
import com.teamquixote.ai.AiGameState;
import com.watabou.input.NoosaInputProcessor;
import com.watabou.pixeldungeon.input.GameAction;
import com.watabou.utils.Random;

public class StupidAgent extends AiAgent {
    public StupidAgent(NoosaInputProcessor processor) {
        super(processor);
    }

    @Override
    protected void makeDecision(AiGameState state) {
        int action = Random.IntRange(1, 5);
        switch(action) {
            case 1:
                doAction(GameAction.MOVE_UP);
                break;
            case 2:
                doAction(GameAction.MOVE_DOWN);
                break;
            case 3:
                doAction(GameAction.MOVE_LEFT);
                break;
            case 4:
                doAction(GameAction.MOVE_RIGHT);
                break;
            case 5:
                doAction(GameAction.BACK);
        }
    }
}
