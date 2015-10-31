package com.teamquixote.ai.actions;

import com.teamquixote.ai.GameState;

public abstract class Action {
    public abstract void execute();

    public int getUpdatedHeroPosition(GameState state) {
        //implementing classes can override this if their actions cause a change in hero location
        return state.heroPosition;
    }
}