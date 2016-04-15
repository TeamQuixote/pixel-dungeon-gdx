package com.teamquixote.ai.heuristic.agents;

import com.teamquixote.ai.heuristic.DungeonMap;
import com.teamquixote.ai.heuristic.GameState;
import com.teamquixote.ai.heuristic.StateSpaceExplorer;

import java.util.List;

public class Spelunker extends AngryFrontiersman {
    public Spelunker(){
        super();
        ss = new StateSpaceExplorer(new GoDeeperAfterExploration());
    }

    public class GoDeeperAfterExploration extends EnemiesOverUnexplored{
        protected double getExitDistance(GameState state){
            List<DungeonMap.TileInfo> exits = state.dungeonMap.find(ti -> ti.isExit(false) && ti.isMapped());
            return exits.size() > 0 ? exits.get(0).getDistance(state.heroPosition) : 1000;
        }

        @Override
        public boolean isTerminalState(GameState state) {
            if (super.isTerminalState(state))
                return true;

            long undiscoveredTileSize = state.dungeonMap.map[state.heroPosition].getAdjacent().stream().filter(ti -> !ti.isMapped()).count();
            boolean adjacentToEnemy = state.visibleEnemies.stream().anyMatch(x -> DungeonMap.MapUtilities.areAdjacent(x.enemyPos, state.heroPosition));

            return (undiscoveredTileSize == 0 && !adjacentToEnemy && getExitDistance(state) == 0);
        }

        @Override
        public double getUtility(GameState state) {
            return super.getUtility(state) + (getExitDistance(state)/1000);
        }
    }
}
