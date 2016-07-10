package com.teamquixote.ai.simulators;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;

public class SimulatorPool {
    private final Path jarPath, saveDirectory;
    public Statistics statistics = new Statistics();

    ExecutorService threadPool;

    public SimulatorPool(Path jarPath, Path saveDirectory) {
        this.jarPath = jarPath;
        this.saveDirectory = saveDirectory;

        this.threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void start(Path loadFile, int numberSimulations) {
        Collection<Callable<Object>> tasks = new ArrayList<>(numberSimulations);
        for (int i = 0; i < numberSimulations; i++)
            tasks.add(Executors.callable(() -> {
                new GameSimulator(jarPath, saveDirectory, loadFile).start();
            }));
        try {
            Instant start = Instant.now();

            threadPool.invokeAll(tasks);

            Instant end = Instant.now();
            statistics.totalDuration += Duration.between(start, end).toMillis();
            statistics.totalSimulations += numberSimulations;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class Statistics {
        int totalSimulations;
        long totalDuration;

        public double getAvgSimulationDuration() {
            if (totalSimulations > 0)
                return (totalDuration * 1.0) / totalSimulations;
            return 0;
        }
    }
}