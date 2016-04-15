package com.teamquixote.ai.heuristic;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Terrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DungeonMap {
    public static int DUNGEON_WIDTH = Level.WIDTH;
    public final TileInfo[] map;

    public DungeonMap() {
        this(Dungeon.level.map, Dungeon.level.visited);
    }

    public DungeonMap(int[] map, boolean[] isMapped) {
        this.map = new TileInfo[map.length];
        for (int i = 0; i < map.length; i++) this.map[i] = new TileInfo(i, map[i], isMapped[i]);
    }

    public List<TileInfo> find(Predicate<TileInfo> selector) {
        List<TileInfo> positives = new ArrayList<>();
        for (TileInfo ti : map)
            if (selector.test(ti))
                positives.add(ti);

        return positives;
    }

    public class TileInfo {
        public final int tilePosition;
        private final int tileValue;
        private final int tileFlag;
        private final boolean isMapped;

        private TileInfo(int tilePosition, int tileValue, boolean isMapped) {
            this.tilePosition = tilePosition;
            this.tileValue = tileValue;
            this.tileFlag = Terrain.flags[tileValue];
            this.isMapped = isMapped;
        }

        public boolean isAdjacentTo(int position) {
            return MapUtilities.areAdjacent(position, tilePosition);
        }

        public List<TileInfo> getAdjacent() {
            return Arrays.stream(MapUtilities.getAdjacent(tilePosition))
                    .filter(i -> i >= 0 && i < map.length)
                    .mapToObj(i -> map[i])
                    .collect(Collectors.toList());
        }

        public double getDistance(int target) {
            return MapUtilities.getDistance(tilePosition, target);
        }

        public boolean isMapped() {
            return isMapped;
        }

        public Boolean isTerrain(int terrainFlag) {
            if (!isMapped())
                return null;
            return (tileFlag & terrainFlag) != 0;
        }

        public boolean isExit(boolean defaultValue){
            return isMapped() ? tileValue == Terrain.EXIT : defaultValue;
        }

        public boolean isTerrain(int terrainFlag, boolean defaultValue) {
            Boolean val = isTerrain(terrainFlag);
            return val == null ? defaultValue : val;
        }
    }

    public static class MapUtilities {
        public static int getRow(int position) {
            return position / DUNGEON_WIDTH;
        }

        public static int getColumn(int position) {
            return position % DUNGEON_WIDTH;
        }

        public static boolean areAdjacent(int position1, int position2) {
            int tileRow = getRow(position1);
            int tileColumn = getColumn(position1);
            int targetRow = getRow(position2);
            int targetColumn = getColumn(position2);

            return Math.abs(tileRow - targetRow) <= 1 && Math.abs(tileColumn - targetColumn) <= 1;
        }

        public static int[] getAdjacent(int position) {
            return new int[]{
                    position - DUNGEON_WIDTH,
                    position - DUNGEON_WIDTH + 1,
                    position + 1,
                    position + 1 + DUNGEON_WIDTH,
                    position + DUNGEON_WIDTH,
                    position - 1 + DUNGEON_WIDTH,
                    position - 1,
                    position - 1 - DUNGEON_WIDTH
            };
        }

        public static double getDistance(int position1, int position2) {
            int dx = getColumn(position1) - getColumn(position2);
            int dy = getRow(position1) - getRow(position2);

            return Math.sqrt((dx * dx) + (dy * dy));
        }
    }
}