package com.teamquixote.ai.launchers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.teamquixote.ai.agents.Randy;
import com.teamquixote.ai.dungeons.PersistentDungeon;
import com.teamquixote.ai.io.GameStateData;

import java.io.IOException;

import static org.mockito.Mockito.mock;

public class PersistentLauncher {
    public static void main(String[] args) throws IOException {
        String saveDirectory = args[0];
        GameStateData gameStateFile;
        if (args.length > 1) {
            String fileName = args[1];
            System.out.println("Loading file: " + fileName);
            gameStateFile = GameStateData.loadFromDisk(fileName);
        } else {
            System.out.println("starting from new game");
            gameStateFile = null;
        }

        //need to mock the GL, as specified in this SO post: http://stackoverflow.com/q/25612660/437456
        Gdx.gl = mock(GL20.class);

        new HeadlessApplication(new PersistentDungeon(new Randy(), saveDirectory, gameStateFile), new HeadlessApplicationConfiguration());

        //use this line below to turn off the game logging
        Gdx.app.setLogLevel(Application.LOG_NONE);
    }
}