package com.teamquixote.ai.heuristic.actions;

import com.teamquixote.ai.heuristic.GameState;
import com.watabou.utils.Bundle;

public abstract class Action {
    public abstract void execute();

    public abstract Bundle toBundle();

    public double getActionCost(GameState state){
        return 1.0;
    }

    public int getUpdatedHeroPosition(GameState state) {
        //implementing classes can override this if their actions cause a change in hero location
        return state.heroPosition;
    }
}