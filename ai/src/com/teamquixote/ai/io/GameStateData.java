package com.teamquixote.ai.io;

import com.teamquixote.ai.actions.Action;
import com.watabou.pixeldungeon.levels.Terrain;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class GameStateData {
    protected JSONObject data;

    public GameStateData() {
        data = new JSONObject();
        setId(UUID.randomUUID());
    }

    public UUID getParentId() {
        return (UUID) data.get("parentId");
    }

    public void setParentId(UUID parentId) {
        data.put("parentId", parentId);
    }

    public void setParentAction(Action action) {
        data.put("parentAction", action.toJSON());
    }

    public UUID getId() {
        Object object = data.get("id");
        if (object instanceof UUID) {
            return (UUID) object;
        }

        return UUID.fromString(object.toString());
    }

    public void setId(UUID id) {
        data.put("id", id);
    }

    public int getHeroPosition() {
        return getHeroData().getInt("pos");
    }

    public int getCurrentLevel() {
        return getHeroMetaData().getInt("depth");
    }

    public boolean isPositionExit(int level, int pos) {
        int posValue = getMapValue(level, pos);
        return posValue == Terrain.EXIT;
    }


    public boolean isPositionExplored(int level, int pos){
        return isPositionVisited(level, pos) || isPositionMapped(level, pos);
    }

    public boolean isPositionMapped(int level, int pos){
        return getLevelData(level).getJSONArray("mapped").getBoolean(pos);
    }

    public boolean isPositionVisited(int level, int pos){
        return getLevelData(level).getJSONArray("visited").getBoolean(pos);
    }

    public boolean isPositionPassable(int level, int pos) {
        return (getPositionFlag(level, pos) & Terrain.PASSABLE) != 0;
    }

    public double calculatePercentExplored() {
        double percentExplored = 0;
        for (int lvl = 1; lvl <= TOTAL_LEVELS; lvl++)
            percentExplored += LEVEL_RATIO * calculatePercentExplored(lvl);

        return percentExplored;
    }
    public double calculatePercentExplored(int level) {
        if (!hasLevelData(level))
            return 0;

        int totalExplored = 0;
        int totalPassable = 0;

        int length = Utilities.DUNGEON_WIDTH * Utilities.DUNGEON_WIDTH;
        for (int i = 0; i < length; i++) {
            if (isPositionExplored(level, i))
                totalExplored++;
            if (isPositionPassable(level, i))
                totalPassable++;
        }

        return 1.0 * totalExplored / totalPassable;
    }

    public static final int TOTAL_LEVELS = 26;
    protected static final double LEVEL_RATIO = 1.0/TOTAL_LEVELS;

    protected String buildLevelKey(int level) {
        return getHeroClassLabel() + level + ".dat";
    }
    protected boolean hasLevelData(int level) {
        return data.has(buildLevelKey(level));
    }
    protected JSONObject getLevelData(int level) {
        return data.getJSONObject(buildLevelKey(level)).getJSONObject("level");
    }

    /**
     * TODO: parameterize this based on hero's class (if we ever get bored playing as the warrior"
     */
    protected String getHeroClassLabel(){
        return "warrior";
    }

    /**
     * returns json pertaining to warrior state (location e.g. data["warrior.dat"])
     * @return
     */
    protected JSONObject getHeroMetaData(){
        return data.getJSONObject(getHeroClassLabel() + ".dat");
    }

    /**
     * return json pertaining to the data specific to the hero (location e.g. data["warrior.dat"]["hero"])
     * @return
     */
    protected JSONObject getHeroData() {
        return getHeroMetaData().getJSONObject("hero");
    }

    protected JSONArray getMapValues(int level){
        return getLevelData(level)
                .getJSONArray("map");
    }

    /**
     * returns the equivalent of Dungeon.level.map[pos]
     *
     * @param pos
     * @return
     */
    protected int getMapValue(int level, int pos) {
        return getMapValues(level).getInt(pos);
    }

    protected int getPositionFlag(int level, int pos) {
        return Terrain.flags[getMapValue(level, pos)];
    }

    public GameStateData copy(){
        GameStateData copy = new GameStateData();
        copy.data = new JSONObject(data.toString());

        return copy;
    }

    public GameStateData createChild(Action action) {
        GameStateData copy = copy();
        copy.setParentAction(action);
        copy.setParentId(copy.getId());
        copy.setId(UUID.randomUUID());

        return copy;
    }

    public OutputStream saveSection(String sectionName) {
        return new JsonObjectOutputStream(sectionName, data);
    }

    public InputStream loadSection(String sectionName) throws IOException {
        try {
            return new ByteArrayInputStream(data.getString(sectionName).getBytes());
        } catch (JSONException jsonE) {
            throw new IOException("File " + sectionName + " doesn't exist");
        }
    }

    public void saveToDisk(String fileName) throws IOException {
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(data.toString());
        }
    }

    public static GameStateData loadFromDisk(String fileName) throws IOException {
        GameStateData gsd = new GameStateData();
        Path filePath = Paths.get(fileName);
        byte[] bytes = Files.readAllBytes(filePath);
        String fileString = new String(bytes);
        gsd.data = new JSONObject(fileString);
        return gsd;
    }

    private class JsonObjectOutputStream extends OutputStream implements AutoCloseable {

        private final String key;
        private final JSONObject data;

        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        public JsonObjectOutputStream(String key, JSONObject data) {
            this.key = key;
            this.data = data;
        }

        @Override
        public void write(int b) throws IOException {
            buffer.write(b);
        }

        @Override
        public void flush() {
            JSONObject flushed = new JSONObject(buffer.toString());
            data.put(key, flushed);
        }

        @Override
        public void close() {
            flush();
        }
    }

    public static class Utilities {
        public static int DUNGEON_WIDTH = 32;

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

        public static int getDx(int start, int end) {
            return (end % DUNGEON_WIDTH)- (start % DUNGEON_WIDTH);
        }

        public static int getDy(int start, int end) {
            return (end / DUNGEON_WIDTH) - (start / DUNGEON_WIDTH);
        }
    }
}