package com.teamquixote.ai.launchers;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.teamquixote.ai.agents.Randy;
import com.teamquixote.ai.dungeons.PersistentDungeon;
import com.teamquixote.ai.io.GameStateData;

import java.io.IOException;

public class PersistentLauncher {
    public static void main(String[] args) throws IOException {
        String saveDirectory = args[0];
        GameStateData gameStateFile = args.length > 1 ? GameStateData.loadFromDisk(args[1]) : null;

        new LwjglApplication(new PersistentDungeon(new Randy(), saveDirectory, gameStateFile), new LwjglApplicationConfiguration());
    }
}