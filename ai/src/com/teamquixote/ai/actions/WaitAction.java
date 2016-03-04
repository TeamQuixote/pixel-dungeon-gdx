package com.teamquixote.ai.actions;

import com.watabou.pixeldungeon.Dungeon;

public class WaitAction extends Action {

    @Override
    public void execute() {
        Dungeon.getInstance().hero.rest(false);
    }

    @Override
    public String describeAction() {
        return "Waiting...";
    }
}
