package com.teamquixote.ai.launchers;

import com.teamquixote.ai.io.GameStateDataFileWatchService;

import java.io.IOException;

public class GameStateTreeListener {
    public static void main(String[] args) throws IOException {
        new GameStateDataFileWatchService(args[0]);
    }
}
