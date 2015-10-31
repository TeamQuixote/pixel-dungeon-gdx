package com.teamquixote.ai.test;

import com.teamquixote.ai.DungeonMap;

import static org.junit.Assert.*;

public class MapUtilitiesTest {

    @org.junit.Test
    public void testAreAdjacent() throws Exception {
        //2nd row, middle column so we have 8 directions to test positive for
        final int pos = DungeonMap.DUNGEON_WIDTH + (DungeonMap.DUNGEON_WIDTH / 2);

        //above
        assertTrue(areAdjacent(pos, pos - DungeonMap.DUNGEON_WIDTH));
        assertTrue(areAdjacent(pos - DungeonMap.DUNGEON_WIDTH, pos));

        //upper right
        assertTrue(areAdjacent(pos, pos - DungeonMap.DUNGEON_WIDTH + 1));
        assertTrue(areAdjacent(pos - DungeonMap.DUNGEON_WIDTH + 1, pos));

        //right
        assertTrue(areAdjacent(pos, pos + 1));
        assertTrue(areAdjacent(pos + 1, pos));

        //lower right
        assertTrue(areAdjacent(pos, pos + DungeonMap.DUNGEON_WIDTH + 1));
        assertTrue(areAdjacent(pos + DungeonMap.DUNGEON_WIDTH + 1, pos));

        //down
        assertTrue(areAdjacent(pos, pos + DungeonMap.DUNGEON_WIDTH));
        assertTrue(areAdjacent(pos + DungeonMap.DUNGEON_WIDTH, pos));

        //lower left
        assertTrue(areAdjacent(pos, pos + DungeonMap.DUNGEON_WIDTH - 1));
        assertTrue(areAdjacent(pos + DungeonMap.DUNGEON_WIDTH - 1, pos));

        //left
        assertTrue(areAdjacent(pos, pos - 1));
        assertTrue(areAdjacent(pos - 1, pos));

        //upper left
        assertTrue(areAdjacent(pos, pos - DungeonMap.DUNGEON_WIDTH - 1));
        assertTrue(areAdjacent(pos - DungeonMap.DUNGEON_WIDTH - 1, pos));

        //2 rows below
        assertFalse(areAdjacent(pos, pos+DungeonMap.DUNGEON_WIDTH+DungeonMap.DUNGEON_WIDTH));

        //2 columns to the right
        assertFalse(areAdjacent(pos, pos+2));
    }

    private static boolean areAdjacent(int pos1, int pos2) {
        return DungeonMap.MapUtilities.areAdjacent(pos1, pos2);
    }
}