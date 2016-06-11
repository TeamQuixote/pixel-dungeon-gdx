package com.teamquixote.ai.io;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GameStateDataTest {
    private GameStateData getData(String filename) {
        try {
            //I don't know the "right" way to load resource files in Java...
            String filePath = new File("src/com/teamquixote/ai/io/resources/" + filename).getAbsolutePath();
            return GameStateData.loadFromDisk(filePath);
        } catch (IOException e) {
            fail("error loading " + filename);
            return null;
        }
    }

    /**
     * some random game state from playing through
     * @return
     */
    private GameStateData getData1() {
        return getData("data1.json");
    }

    @Test
    public void getHeroPosition() {
        assertEquals(516, getData1().getHeroPosition());
    }

    @Test
    public void isPositionExit() {
        GameStateData data = getData1();
        for (int i = 0; i < 1024; i++)
            //there's only one exit, and it's at position 876
            assertTrue(i != 876 || data.isPositionExit(i));
    }

    @Test
    public void isPositionPassable(){
        GameStateData data = getData1();
        List<Integer> passables = new ArrayList<>();
        for (int i = 0; i < 1024; i++) {
            if (data.isPositionPassable(i))
                passables.add(i);
        }
        /*
        kinda lazy tests, but just saying that it'll return not an all-or-nothing value (which implies that it's kind of
        at least doing something other than outright failure) A better test would show that a specific map value is
        passable
         */
        assertNotEquals(0, passables.size());
        assertNotEquals(1024, passables.size());
    }

    @Test
    public void getDx() {
        assertEquals(-1, GameStateData.Utilities.getDx(50, 49));
        assertEquals(1, GameStateData.Utilities.getDx(50, 51));
        assertEquals(-1, GameStateData.Utilities.getDx(50, 17));
        assertEquals(1, GameStateData.Utilities.getDx(50, 83));
    }

    @Test
    public void getDy(){
        assertEquals(1, GameStateData.Utilities.getDy(10, 42));
        assertEquals(1, GameStateData.Utilities.getDy(10, 44));
        assertEquals(2, GameStateData.Utilities.getDy(10, 74));
        assertEquals(-1, GameStateData.Utilities.getDy(32, 0));
    }
}