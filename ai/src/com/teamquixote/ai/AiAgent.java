package com.teamquixote.ai;

import com.watabou.input.NoosaInputProcessor;
import com.watabou.pixeldungeon.input.GameAction;

public abstract class AiAgent {
    private NoosaInputProcessor processor;

    public AiAgent(NoosaInputProcessor processor){
        this.processor = processor;
    }

    public void update(AiGameState state) {
        makeDecision(state);
    }

    protected void doAction(GameAction action) {
        processor.keyDown(AiKeyCodes.getInstance().getActionKey(action));
        processor.keyUp(AiKeyCodes.getInstance().getActionKey(action));
    }


    protected abstract void makeDecision(AiGameState state);
}
