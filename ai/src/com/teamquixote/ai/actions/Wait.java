package com.teamquixote.ai.actions;

import com.teamquixote.ai.io.GameStateData;
import com.watabou.pixeldungeon.Dungeon;

public class Wait extends Action {
    @Override
    public String getType() {
        return "wait";
    }

    @Override
    public void execute(GameStateData gameStateData) {
        Dungeon.hero.rest(false);
    }
}
