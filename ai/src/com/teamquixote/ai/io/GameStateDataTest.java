package com.teamquixote.ai.io;

import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class GameStateDataTest {
    private class GameStateDataTestWrapper extends GameStateData {
        public GameStateDataTestWrapper(GameStateData gsd) {
            this.data = gsd.data;
        }

        public void setMapValue(int level, int position, int value) {
            JSONObject lvlData = getLevelData(level);
            if (lvlData != null)
                lvlData.getJSONArray("map").put(position, value);
        }

        public void setIsMapped(int level, int position, boolean mapped) {
            JSONObject lvlData = getLevelData(level);
            if (lvlData != null)
                lvlData.getJSONArray("mapped").put(position, mapped);
        }

        public void setIsVisited(int level, int position, boolean visited) {
            getLevelData(level).getJSONArray("visited").put(position, visited);
        }
    }

    private GameStateDataTestWrapper getData(String filename) {
        try {
            //I don't know the "right" way to load resource files in Java...
            String filePath = new File("src/com/teamquixote/ai/io/resources/" + filename).getAbsolutePath();
            return new GameStateDataTestWrapper(GameStateData.loadFromDisk(filePath));
        } catch (IOException e) {
            fail("error loading " + filename);
            return null;
        }
    }

    /**
     * some random game state from playing through
     *
     * @return
     */
    private GameStateDataTestWrapper getData1() {
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
            assertTrue(i != 876 || data.isPositionExit(data.getCurrentLevel(), i));
    }

    private static final int[] allPassableValues = new int[]{1, 2, 3, 5, 6, 7, 8, 9, 11, 14, 15, 18, 20, 22, 23, 24, 26, 28, 29, 31, 33, 38, 40, 42, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63};

    @Test
    public void isPositionPassable() {
        GameStateDataTestWrapper data = getData1();
        int lvl = data.getCurrentLevel();

        for (int p : allPassableValues) {
            data.setMapValue(lvl, 0, p);
            assertTrue(data.isPositionPassable(lvl, 0));
        }
    }

    @Test
    public void calculatePercentExplored_allMapped(){
        GameStateDataTestWrapper data = getData1();
        int lvl = data.getCurrentLevel();
        for (int i = 0; i < data.getMapValues(lvl).length(); i++) {
            data.setMapValue(lvl, i, allPassableValues[0]);
            data.setIsMapped(lvl, i, true);
        }

        assertEquals(1, data.calculatePercentExplored(lvl), 0.0001);
        assertEquals(GameStateData.LEVEL_RATIO, data.calculatePercentExplored(), 0.0001);
    }

    @Test
    public void calculatePercentExplored_allVisited(){
        GameStateDataTestWrapper data = getData1();
        int lvl = data.getCurrentLevel();
        for (int i = 0; i < data.getMapValues(lvl).length(); i++) {
            data.setMapValue(lvl, i, allPassableValues[0]);
            data.setIsVisited(lvl, i, true);
        }

        assertEquals(1, data.calculatePercentExplored(lvl), 0.0001);
        assertEquals(GameStateData.LEVEL_RATIO, data.calculatePercentExplored(), 0.0001);
    }

    @Test
    public void calculatePercentExplored_onlyCountsPassable(){
        GameStateDataTestWrapper data = getData1();
        int nonPassableValue = 4;
        int lvl = data.getCurrentLevel();
        //setting half the map as passable and the other half as not passable, and only visiting the passable values
        for (int i = 0; i < data.getMapValues(lvl).length(); i++) {
            if (i % 2 == 0) {
                data.setMapValue(lvl, i, nonPassableValue);
                data.setIsVisited(lvl, i, false);
            } else {
                data.setMapValue(lvl, i, allPassableValues[0]);
                data.setIsVisited(lvl, i, true);
            }
        }

        assertEquals(1, data.calculatePercentExplored(lvl), 0.0001);
        assertEquals(GameStateData.LEVEL_RATIO, data.calculatePercentExplored(), 0.0001);
    }


    @Test
    public void getDx() {
        assertEquals(-1, GameStateData.Utilities.getDx(50, 49));
        assertEquals(1, GameStateData.Utilities.getDx(50, 51));
        assertEquals(-1, GameStateData.Utilities.getDx(50, 17));
        assertEquals(1, GameStateData.Utilities.getDx(50, 83));
    }

    @Test
    public void getDy() {
        assertEquals(1, GameStateData.Utilities.getDy(10, 42));
        assertEquals(1, GameStateData.Utilities.getDy(10, 44));
        assertEquals(2, GameStateData.Utilities.getDy(10, 74));
        assertEquals(-1, GameStateData.Utilities.getDy(32, 0));
    }
}