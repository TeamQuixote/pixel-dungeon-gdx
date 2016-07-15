package com.teamquixote.ai.launchers;

import com.teamquixote.ai.simulators.GameSimulator;
import com.teamquixote.ai.simulators.SimulatorPool;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SimulatorLauncher {

    public static void main(String[] args) {
        Path jarPath = Paths.get("C:\\development\\pixel-dungeon-gdx\\ai\\build\\libs\\ai-1.7.2a-1.jar");
        Path saveDirectory = Paths.get("C:\\temp\\pixel-dungeon-saves");
        Path loadPath = saveDirectory.resolve("5a98090b-5296-4425-8c7b-01acf81a2472");

        SimulatorPool simulators = new SimulatorPool(jarPath, saveDirectory);

        System.out.println("Starting simulation");
        simulators.start(loadPath, 30, 100);
        System.out.println("Finished simulation");
    }
}
