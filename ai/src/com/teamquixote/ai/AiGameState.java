package com.teamquixote.ai;

public class AiGameState {
    private TerrainInfo[] immediateTerrain;

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
