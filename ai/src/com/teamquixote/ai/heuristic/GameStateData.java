package com.teamquixote.ai.heuristic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class GameStateData {
    private JSONObject data;

    public GameStateData() {
        data = new JSONObject();
    }

    public GameStateData(GameStateData original) {
        String origString = original.data.toString();
        data = new JSONObject(origString);
    }

    public GameStateData copy(){
        return new GameStateData(this);
    }

    public OutputStream saveSection(String sectionName) {
        return new JsonObjectOutputStream(sectionName, data);
    }

    public InputStream loadSection(String sectionName) throws IOException {
        try {
            return new ByteArrayInputStream(data.getString(sectionName).getBytes());
        } catch (JSONException jsonE) {
            throw new IOException("File " + sectionName + " doesn't exist");        }
    }

    public void saveToDisk(String fileName) throws IOException {
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(data.toString());
        }
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
            String flushed = buffer.toString();
            data.put(key, flushed);
        }

        @Override
        public void close() {
            flush();
        }
    }
}