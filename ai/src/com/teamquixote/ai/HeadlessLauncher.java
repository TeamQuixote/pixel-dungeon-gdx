package com.teamquixote.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.teamquixote.ai.agents.Randy;
import com.teamquixote.ai.agents.Spelunker;
import com.watabou.utils.PDPlatformSupport;

import static org.mockito.Mockito.mock;

public class HeadlessLauncher {

    public static void main(String[] args) {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();

        //need to mock the GL, as specified in this SO post: http://stackoverflow.com/q/25612660/437456
        Gdx.gl = mock(GL20.class);
        new HeadlessApplication(new AiPixelDungeon(new HeadlessSupport(), new Randy()), config);

        //use this line below to turn off the game logging
        //Gdx.app.setLogLevel(Application.LOG_NONE);
    }

    private static class HeadlessSupport extends PDPlatformSupport {
        public HeadlessSupport() {
            super("0.0", ".", new AiInputProcessor());
        }

        @Override
        public boolean isFullscreenEnabled() {
            //	return Display.getPixelScaleFactor() == 1f;
            return !SharedLibraryLoader.isMac;
        }
    }

}