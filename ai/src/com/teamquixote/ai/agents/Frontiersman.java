package com.teamquixote.ai.agents;

import com.teamquixote.ai.*;
import com.teamquixote.ai.actions.Action;
import com.teamquixote.ai.actions.WaitAction;
import com.watabou.pixeldungeon.levels.Terrain;

import java.util.*;

public class Frontiersman extends AiAgent {
    protected StateSpaceExplorer ss;
    private Deque<GameState> plannedActions = new ArrayDeque<>();

    public Frontiersman() {
        ss = new StateSpaceExplorer(new TerminateOnUndiscovered());
    }

    @Override
    protected Action makeDecision(GameState state) {
//        int remaining = state.dungeonMap
//                .find(ti -> !ti.isMapped() && ti.getAdjacent()
//                        .stream()
//                        .anyMatch(adj -> adj.isTerrain(Terrain.PASSABLE, false)))
//                .size();
//
//        if (remaining == 0)
//            return new WaitAction();

        GameState expectedState = plannedActions.size() > 0 ? plannedActions.pop() : null;

        if (plannedActions.size() == 0 || !expectedState.equals(state)) {
            plannedActions.clear();
            GameState bestState = ss.findBestState(state);
            if (bestState == null)
                plannedActions.push(new GameState(state, new WaitAction()));
            else while (bestState.previousAction != null) {
                plannedActions.push(bestState);
                bestState = bestState.previousGameState;
            }

        }

        return plannedActions.peek().previousAction;
    }

    public static class TerminateOnUndiscovered extends GameStateUtility {

        @Override
        public boolean isTerminalState(GameState state) {
            long undiscoveredTileSize = state.dungeonMap.map[state.heroPosition].getAdjacent().stream().filter(ti -> !ti.isMapped()).count();
            return undiscoveredTileSize > 0;
        }

        @Override
        public double getUtility(GameState state) {
            List<DungeonMap.TileInfo> undiscovered = state.dungeonMap.find(ti -> !ti.isMapped() && ti.getAdjacent().stream().anyMatch(adj -> adj.isTerrain(Terrain.PASSABLE, false)));
            OptionalDouble closestUndiscovered = undiscovered.stream().mapToDouble(ti -> ti.getDistance(state.heroPosition)).min();

            return closestUndiscovered.isPresent() ? closestUndiscovered.getAsDouble() : 100;
        }
    }
}