package com.teamquixote.ai.dungeons;

import com.teamquixote.ai.agents.AiAgent;
import com.teamquixote.ai.io.GameStateData;
import com.teamquixote.ai.launchers.EmptyPlatformSupport;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.scenes.InterlevelScene;
import com.watabou.pixeldungeon.scenes.StartScene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * saves all game states generated from the initial state
 */
public class PersistentDungeon extends AiPixelDungeon {
    private GameStateData dataToLoad;
    private final String saveDirectory;

    private List<GameStateData> savedStates = new ArrayList<>();

    private HeroClass getHeroClass(GameStateData gameStateData) {
        String heroClassLabel = gameStateData.getHeroClassLabel();
        switch (heroClassLabel) {
            case "warrior":
                return HeroClass.WARRIOR;
            default:
                throw new IllegalArgumentException("Unknown hero class: " + heroClassLabel);
        }
    }

    public PersistentDungeon(AiAgent ai, String saveDirectory, GameStateData initialState) {
        super(new EmptyPlatformSupport(), ai);

        dataToLoad = initialState;
        this.saveDirectory = saveDirectory + (saveDirectory.endsWith("\\") ? "" : "\\");
    }

    @Override
    protected void dungeonInit() {
        if (dataToLoad == null)
            super.dungeonInit();
        else {
            Badges.loadGlobal();
            StartScene.curClass = getHeroClass(dataToLoad);
            this.currentState = dataToLoad;
            InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
            Game.switchScene(InterlevelScene.class);
        }
    }

    @Override
    protected void stateChanged(GameStateData state) {
        savedStates.add(state);
    }

    @Override
    protected void heroDied() {
        for (GameStateData d : savedStates) {
            try {
                d.saveToDisk(saveDirectory + d.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}