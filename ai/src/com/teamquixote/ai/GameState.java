package com.teamquixote.ai;

import com.teamquixote.ai.actions.Action;
import com.teamquixote.ai.actions.MoveAction;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {
    public final GameState previousGameState;
    public final Action previousAction;
    public final int heroPosition;
    public final DungeonMap dungeonMap;
    public final List<DungeonEnemy> visibleEnemies;

    public GameState() {
        this.previousGameState = null;
        this.previousAction = null;
        heroPosition = Dungeon.getInstance().hero.pos;
        dungeonMap = new DungeonMap();
        this.visibleEnemies = buildVisibleEnemies();

    }

    public GameState(GameState state, Action action) {
        this.previousGameState = state;
        this.previousAction = action;
        this.heroPosition = action.getUpdatedHeroPosition(state);
        this.dungeonMap = state.dungeonMap;
        this.visibleEnemies = state.visibleEnemies;
    }

    private List<DungeonEnemy> buildVisibleEnemies(){
        Level level = Dungeon.getInstance().level;
        return level.mobs.stream()
                .filter(enemy -> enemy.isAlive() && level.fieldOfView[enemy.pos] && enemy.invisible <= 0)
                .map(mob -> new DungeonEnemy(mob.pos))
                .collect(Collectors.toList());
    }

    public List<Action> getActions() {
        List<Action> validActions = new ArrayList<>();
        if(dungeonMap.map[heroPosition].isExit(false))
            return validActions;

        for (DungeonMap.TileInfo ti : dungeonMap.map[heroPosition].getAdjacent())
            if (ti.isTerrain(Terrain.PASSABLE, false))
                validActions.add(new MoveAction(ti.tilePosition));

        return validActions;
    }

    @Override
    public boolean equals(Object obj) {
        if(!this.getClass().isInstance(obj))
            return false;

        if(((GameState)obj).visibleEnemies.size() != visibleEnemies.size())
            return false;

        for(int i = 0; i < visibleEnemies.size(); i++){
            if(!((GameState)obj).visibleEnemies.get(i).equals(visibleEnemies.get(i)))
                return false;
        }

        return ((GameState)obj).heroPosition == heroPosition;
    }

    @Override
    public int hashCode() {
        int enemyHash = 0x0;
        for(DungeonEnemy enemy : visibleEnemies){
            enemyHash ^= enemy.hashCode();
        }
        return heroPosition ^ enemyHash;
    }

}