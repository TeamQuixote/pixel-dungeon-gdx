package com.teamquixote.ai;

import com.teamquixote.ai.actions.Action;
import com.teamquixote.ai.actions.MoveAction;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.levels.Terrain;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    public final GameState previousGameState;
    public final Action previousAction;
    public final int heroPosition;
    public final DungeonMap dungeonMap;

    public GameState() {
        this.previousGameState = null;
        this.previousAction = null;
        heroPosition = Dungeon.hero.pos;
        dungeonMap = new DungeonMap();
    }

    public GameState(GameState state, Action action) {
        this.previousGameState = state;
        this.previousAction = action;
        this.heroPosition = action.getUpdatedHeroPosition(state);
        this.dungeonMap = state.dungeonMap;
    }

    public List<Action> getActions() {
        List<Action> validActions = new ArrayList<>();
        for (DungeonMap.TileInfo ti : dungeonMap.map) {
            boolean isAdjacent = DungeonMap.MapUtilities.areAdjacent(heroPosition, ti.tilePosition);
            Boolean isPassable = ti.isTerrain(Terrain.PASSABLE);
            if (isAdjacent && isPassable != null && isPassable)
                validActions.add(new MoveAction(ti.tilePosition));
        }

        return validActions;
    }

    @Override
    public boolean equals(Object obj) {
        if(!this.getClass().isInstance(obj))
            return false;

        return ((GameState)obj).heroPosition == heroPosition;
    }

    @Override
    public int hashCode() {
        return heroPosition;
    }
}