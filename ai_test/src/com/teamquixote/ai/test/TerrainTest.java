package com.teamquixote.ai.test;

import com.teamquixote.ai.DungeonMap;
import com.watabou.pixeldungeon.levels.Terrain;

import static org.junit.Assert.*;

public class TerrainTest {
    @org.junit.Test
    public void testIsDoorPassable() throws Exception {
        DungeonMap dm = new DungeonMap(new int[]{
                Terrain.DOOR,
                Terrain.LOCKED_DOOR
        }, new boolean[]{true, true});

        assertTrue(dm.map[0].isTerrain(Terrain.PASSABLE));
        assertFalse(dm.map[1].isTerrain(Terrain.PASSABLE));
    }
}