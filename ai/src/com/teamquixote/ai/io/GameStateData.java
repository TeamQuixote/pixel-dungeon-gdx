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
    private JSONObject data;

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

    public boolean isPositionExit(int pos) {
        int posValue = getPositionValue(pos);
        return posValue == Terrain.EXIT;
    }

    public boolean isPositionPassable(int pos) {
        return (getPositionFlag(pos) & Terrain.PASSABLE) != 0;
    }

    /**
     * TODO: parameterize this based on hero's class (if we ever get bored playing as the warrior"
     */
    private static final String heroClassLabel = "warrior";

    private JSONObject getHeroData() {
        return data.getJSONObject(heroClassLabel + ".dat").getJSONObject("hero");
    }

    /**
     * returns the equivalent of Dungeon.level.map[pos]
     *
     * @param pos
     * @return
     */
    private int getPositionValue(int pos) {
        int currentLevel = getHeroData().getInt("lvl");
        return data.getJSONObject(heroClassLabel + currentLevel + ".dat")
                .getJSONObject("level")
                .getJSONArray("map")
                .getInt(pos);
    }

    private int getPositionFlag(int pos) {
        return Terrain.flags[getPositionValue(pos)];
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