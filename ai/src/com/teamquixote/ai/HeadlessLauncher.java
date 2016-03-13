package com.teamquixote.ai;

import static org.mockito.Mockito.mock;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.teamquixote.ai.agents.Spelunker;
import com.watabou.utils.PDPlatformSupport;

public class HeadlessLauncher {

    public static void main(String[] args) {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();

        //I'm not entirely sure if this does anything, but seems like it should... will do more testing to see
        config.renderInterval = 0.000000001f;

        //need to mock the GL, as specified in this SO post: http://stackoverflow.com/q/25612660/437456
        Gdx.gl = mock(GL20.class);
        new HeadlessApplication(new AiPixelDungeon(new Spelunker()), config);

        //use this line below to turn off the game logging
        //Gdx.app.setLogLevel(Application.LOG_NONE);
    }
}
