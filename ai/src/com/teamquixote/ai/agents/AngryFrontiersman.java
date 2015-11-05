package com.teamquixote.ai.agents;

import com.teamquixote.ai.DungeonMap;
import com.teamquixote.ai.GameState;
import com.teamquixote.ai.GameStateUtility;
import com.teamquixote.ai.StateSpaceExplorer;

public class AngryFrontiersman extends Frontiersman {
    public AngryFrontiersman(){
        super();
        ss = new StateSpaceExplorer(new EnemiesOverUnexplored());
    }

    public class EnemiesOverUnexplored extends Frontiersman.TerminateOnUndiscovered {

        @Override
        public boolean isTerminalState(GameState state) {
            boolean adjacentToEnemy = state.visibleEnemies.stream().anyMatch(x -> DungeonMap.MapUtilities.areAdjacent(x.enemyPos, state.heroPosition));
            return super.isTerminalState(state) || adjacentToEnemy;
        }

        @Override
        public double getUtility(GameState state) {
            double minEnemyDistance = state.visibleEnemies.stream()
                    .mapToDouble(x -> DungeonMap.MapUtilities.getDistance(x.enemyPos, state.heroPosition))
                    .min().orElse(100);
            return super.getUtility(state) / 100 + minEnemyDistance;
        }
    }
}
