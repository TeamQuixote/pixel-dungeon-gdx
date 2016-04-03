package com.teamquixote.ai;

public class AiPixelDungeonConfig {
    public final String GameStateFileDirectory;

    public final String LoadFromGameStateFile;

    public AiPixelDungeonConfig(){
        this(null, null);
    }

    public AiPixelDungeonConfig(String gameStateFileDirectory, String loadFromGameStateFile) {
        GameStateFileDirectory = gameStateFileDirectory;
        LoadFromGameStateFile = loadFromGameStateFile;
    }
}