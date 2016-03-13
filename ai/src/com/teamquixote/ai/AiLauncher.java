package com.teamquixote.ai;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglPreferences;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.teamquixote.ai.agents.AngryFrontiersman;
import com.teamquixote.ai.agents.Frontiersman;
import com.teamquixote.ai.agents.Spelunker;
import com.watabou.input.NoosaInputProcessor;
import com.watabou.pixeldungeon.Preferences;
import com.watabou.utils.PDPlatformSupport;

public class AiLauncher {
	public static void main (String[] arg) {
		String version = AiLauncher.class.getPackage().getSpecificationVersion();
		if (version == null) {
			version = "???";
		}
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		if (SharedLibraryLoader.isMac) {
			config.preferencesDirectory = "Library/Application Support/Pixel Dungeon/";
		} else if (SharedLibraryLoader.isLinux) {
			config.preferencesDirectory = ".watabou/pixel-dungeon/";
		} else if (SharedLibraryLoader.isWindows) {
			config.preferencesDirectory = "Saved Games/";
		}
		// FIXME: This is a hack to get access to the preferences before we have an application setup
		com.badlogic.gdx.Preferences prefs = new LwjglPreferences(Preferences.FILE_NAME, config.preferencesDirectory);

		boolean isFullscreen = prefs.getBoolean(Preferences.KEY_WINDOW_FULLSCREEN, false);
		config.fullscreen = isFullscreen;
		if (!isFullscreen) {
			config.width = prefs.getInteger(Preferences.KEY_WINDOW_WIDTH, Preferences.DEFAULT_WINDOW_WIDTH);
			config.height = prefs.getInteger(Preferences.KEY_WINDOW_HEIGHT, Preferences.DEFAULT_WINDOW_HEIGHT);
		}

		config.addIcon( "ic_launcher_128.png", Files.FileType.Internal );
		config.addIcon( "ic_launcher_32.png", Files.FileType.Internal );
		config.addIcon( "ic_launcher_16.png", Files.FileType.Internal );


		// TODO: It have to be pulled from build.gradle, but I don't know how it can be done
		config.title = "Pixel Dungeon";
		new LwjglApplication(new AiPixelDungeon(new Spelunker()), config);
	}
}
