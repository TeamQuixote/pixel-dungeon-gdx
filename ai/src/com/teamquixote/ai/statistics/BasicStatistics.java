package com.teamquixote.ai.statistics;

import com.teamquixote.ai.AiAgent;
import com.teamquixote.ai.DungeonMap;
import com.teamquixote.ai.GameState;
import com.watabou.pixeldungeon.levels.Terrain;

public class BasicStatistics implements GameStatistics {
    private int moveCount = 0;

    @Override
    public void onUpdate(AiAgent agent, GameState state) {
        moveCount++;

        int totalExplored = state.dungeonMap.find(DungeonMap.TileInfo::isMapped).size();
        int totalKnownPassable = state.dungeonMap.find(ti -> ti.isTerrain(Terrain.PASSABLE, false)).size();
        int totalUnexploredReachable = state.dungeonMap
                .find(ti -> !ti.isMapped() && ti.getAdjacent()
                        .stream()
                        .anyMatch(adj -> adj.isTerrain(Terrain.PASSABLE, false)))
                .size();
        System.out.println(moveCount + "\tE: " + totalExplored + "\tP: " + totalKnownPassable + "\tR: " + totalUnexploredReachable);
    }
}
