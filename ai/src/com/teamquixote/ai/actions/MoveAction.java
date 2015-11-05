package com.teamquixote.ai.actions;

import com.teamquixote.ai.GameState;
import com.watabou.pixeldungeon.Dungeon;

public class MoveAction extends Action {
    private final int target;

    public MoveAction(int target) {
        this.target = target;
    }

    @Override
    public int getUpdatedHeroPosition(GameState state){
        return target;//should probably do some error checking or whatever
    }

    @Override
    public void execute() {
        //stole this from GameScene.defaultCellListener
        if (Dungeon.hero.handle(target)) {
            Dungeon.hero.next();
        }
    }
}