package com.teamquixote.ai;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.levels.Level;

import java.util.List;
import java.util.stream.Collectors;

public class AiGameState {
    private TerrainInfo[] immediateTerrain;
    private List<Integer> mobLocations;
    private int heroPosition;

    public int getHeroPosition(){
        return this.heroPosition;
    }

    public void setHeroPosition(int heroPosition){
        this.heroPosition = heroPosition;
    }

    public List<Integer> getMobLocations() {
        if (mobLocations == null) mobLocations = Dungeon.level.mobs
                .stream()
                .filter(enemy -> enemy.isAlive() && Level.fieldOfView[enemy.pos] && enemy.invisible <= 0)
                .mapToInt(enemy -> enemy.pos)
                .boxed()
                .collect(Collectors.toList());

        return mobLocations;
    }

    public void setImmediateTerrain(TerrainInfo n,
                                     TerrainInfo nw,
                                     TerrainInfo w,
                                     TerrainInfo sw,
                                     TerrainInfo s,
                                     TerrainInfo se,
                                     TerrainInfo e,
                                     TerrainInfo ne ) {
        immediateTerrain = new TerrainInfo[8];
        immediateTerrain[Direction.North.ordinal()] = n;
        immediateTerrain[Direction.NorthWest.ordinal()] = nw;
        immediateTerrain[Direction.West.ordinal()] = w;
        immediateTerrain[Direction.SouthWest.ordinal()] = sw;
        immediateTerrain[Direction.South.ordinal()] = s;
        immediateTerrain[Direction.SouthEast.ordinal()] = se;
        immediateTerrain[Direction.East.ordinal()] = e;
        immediateTerrain[Direction.NorthEast.ordinal()] = ne;
    }

    public TerrainInfo getImmediateTerrain(Direction direction){
        return immediateTerrain[direction.ordinal()];
    }

    public TerrainInfo getRelativeTerrain(Direction facing, RelativeDirection relative){
        return immediateTerrain[Direction.getRelativeDirection(facing, relative).ordinal()];
    }


}
