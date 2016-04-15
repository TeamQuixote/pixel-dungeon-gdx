package com.teamquixote.ai.heuristic.actions;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.utils.Bundle;

public class WaitAction extends Action {

    @Override
    public void execute() {
        Dungeon.hero.rest(false);
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.put("name", "wait");

        return bundle;
    }
}
