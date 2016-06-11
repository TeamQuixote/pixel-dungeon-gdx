package com.teamquixote.ai.dungeons;

import com.teamquixote.ai.agents.AiAgent;
import com.teamquixote.ai.io.GameStateData;
import com.teamquixote.ai.launchers.EmptyPlatformSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * saves all game states generated from the initial state
 */
public class PersistentDungeon extends AiPixelDungeon {
    private final String saveDirectory;

    private List<GameStateData> savedStates = new ArrayList<>();

    public PersistentDungeon(AiAgent ai, String saveDirectory, GameStateData initialState) {
        super(new EmptyPlatformSupport(), ai);

//        this.stateData = initialState == null ? new GameStateData() : initialState;
        this.saveDirectory = saveDirectory + (saveDirectory.endsWith("\\") ? "" : "\\");
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