package com.teamquixote.ai.simulators;

import java.nio.file.Path;

/**
 * Spawns process to play the game with the given jar
 */
public class GameSimulator {
    private final String command;

    public GameSimulator(Path jarPath, Path saveDirectory, Path filePath) {
        this.command = String.format("java -jar %s %s %s", jarPath, saveDirectory, filePath == null ? "" : filePath.toString());
    }

    public GameSimulator(Path jarPath, Path saveDirectory) {
        this(jarPath, saveDirectory, null);
    }

    public void start(){
        start(true);
    }

    public void start(boolean waitUntilComplete) {
        Runtime rt = Runtime.getRuntime();
        try {
            Process p = rt.exec(command);
            if(waitUntilComplete)
                p.waitFor();
        } catch (Exception e) {
            System.out.println("Error executing command: " + command);
            e.printStackTrace();
        }
    }
}