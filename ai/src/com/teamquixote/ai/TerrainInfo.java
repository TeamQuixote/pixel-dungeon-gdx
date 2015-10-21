package com.teamquixote.ai;

import com.watabou.pixeldungeon.levels.Terrain;

public class TerrainInfo {
    private int terrainValue;

    public TerrainInfo(int fromMapValue){
        terrainValue = fromMapValue;
    }

    public boolean isPassable(){
        return (Terrain.flags[terrainValue] & Terrain.PASSABLE) != 0;
    }

    public boolean isDoor(){
        return terrainValue == Terrain.DOOR || terrainValue == Terrain.OPEN_DOOR;
    }
}
