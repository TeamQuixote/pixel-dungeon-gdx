package com.teamquixote.ai.actions;

import com.teamquixote.ai.DungeonMap;
import com.teamquixote.ai.GameState;
import com.watabou.pixeldungeon.Dungeon;

public class MoveAction extends Action {
    private final int target;

    public MoveAction(int target) {
        this.target = target;
    }

    @Override
    public String describeAction(){
        return String.format("Moving to %d,%d", DungeonMap.MapUtilities.getColumn(target), DungeonMap.MapUtilities.getRow(target));
    }

    @Override
    public int getUpdatedHeroPosition(GameState state) {
        return target;//should probably do some error checking or whatever
    }

    @Override
    public void execute() {
        //stole this from GameScene.defaultCellListener
        if (Dungeon.getInstance().hero.handle(target)) {
            Dungeon.getInstance().nextActor(Dungeon.getInstance().hero);
        }
    }
}