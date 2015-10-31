package com.teamquixote.ai.agents;

import com.teamquixote.ai.*;
import com.teamquixote.ai.actions.Action;
import com.teamquixote.ai.actions.WaitAction;

import java.util.*;

public class Frontiersman extends AiAgent {
    private StateSpaceExplorer ss;
    private Deque<GameState> plannedActions = new ArrayDeque<>();

    public Frontiersman() {
        ss = new StateSpaceExplorer(new TerminateOnUndiscovered());
    }

    @Override
    protected Action makeDecision(GameState state) {
        if (plannedActions.size() == 0 || !plannedActions.peek().equals(state)) {
            plannedActions.clear();
            GameState bestState = ss.findBestState(state);
            if (bestState == null)
                plannedActions.add(new GameState(state, new WaitAction()));
            else while (bestState.previousAction != null) {
                plannedActions.add(bestState);
                bestState = bestState.previousGameState;
            }

        }
        return plannedActions.remove().previousAction;
    }

    public class TerminateOnUndiscovered extends TerminalStateConditions {

        @Override
        public boolean isTerminalState(GameState state) {
            List<DungeonMap.TileInfo> undiscoveredTiles = state.dungeonMap.find(ti -> ti.isAdjacentTo(state.heroPosition) && !ti.isMapped());
            int undiscoveredTileSize = undiscoveredTiles.size();
            return undiscoveredTileSize > 0;
        }
    }
}