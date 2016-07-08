package com.teamquixote.ai.simulators;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;

public class SimulatorPool {
    private final Path jarPath, saveDirectory;

    ExecutorService threadPool;

    public SimulatorPool(Path jarPath, Path saveDirectory) {
        this.jarPath = jarPath;
        this.saveDirectory = saveDirectory;

        this.threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void start(Path loadFile, int numberSimulations) {
        Collection<Callable<Object>> tasks = new ArrayList<>(numberSimulations);
        for (int i = 0; i < numberSimulations; i++) {
            final int finalI = i;
            tasks.add(Executors.callable(() -> {
                new GameSimulator(jarPath, saveDirectory, loadFile).start();
                System.out.println(finalI);
            }));
        }
        try {
            threadPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}