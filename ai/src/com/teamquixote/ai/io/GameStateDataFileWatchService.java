package com.teamquixote.ai.io;

import com.teamquixote.ai.gametree.GameStateTree;
import com.teamquixote.ai.simulators.SimulatorPool;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

/**
 * Crude MVP for a means of listening to a folder and processing files as they come in
 */
public class GameStateDataFileWatchService {
    private GameStateTree tree;
    private SimulatorPool simulatorPool;
    private Path saveDirectory, archiveDirectory;

    private int totalFilesRead = 0;
    private long totalFileProcessTime = 0;

    public GameStateDataFileWatchService(String saveDirectory) throws IOException {
        this.saveDirectory = Paths.get(saveDirectory);
        this.archiveDirectory = this.saveDirectory.resolve("_archive");
        this.tree = new GameStateTree();
        this.simulatorPool = new SimulatorPool(Paths.get("C:\\development\\pixel-dungeon-gdx\\ai\\build\\libs\\ai-1.7.2a-1.jar"),
                this.saveDirectory);

        System.out.format("begin logging stats%n");
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    logStats();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.format("save directory: %s%narchive directory: ", saveDirectory, archiveDirectory);
        new Thread(() -> {
            while (true) {
                processFiles();
                UUID nextSim = tree.chooseNextSimuation();
                Path file = archiveDirectory.resolve(nextSim.toString());
                try {
                    double pe = GameStateData.loadFromDisk(file.toString()).calculatePercentExplored();
                    System.out.println(file + ": " + pe);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                simulatorPool.start(file, 25);
            }
        }).start();
    }

    private void logStats() {
        String filesRead = "Files read: " + totalFilesRead;

        double duration = totalFileProcessTime / 1000.0;

        if (duration == 0)
            System.out.println(filesRead);
        else {
            double filesPerSecond = totalFilesRead / duration;
            System.out.println(filesRead + " Files/sec: " + filesPerSecond);
        }
    }

    private void processFiles() {
        //read all existing files first
        try {
            Files.list(saveDirectory)
                    .filter(Files::isRegularFile)
                    .forEach(this::processFile);
//            Files.walk(saveDirectory).forEach(this::processFile);
//            Files.walk(saveDirectory).forEach(this::archiveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFile(Path newFile) {
        Instant start = Instant.now();
        Path filePath = saveDirectory.resolve(newFile);
        String fullPath = filePath.toAbsolutePath().toString();
        if (Objects.equals(fullPath, saveDirectory.toString()))
            return;
        try {
            GameStateData gsd = GameStateData.loadFromDisk(fullPath);
            tree.add(gsd);

            totalFilesRead++;
            Instant end = Instant.now();
            totalFileProcessTime += Duration.between(start, end).toMillis();
        } catch (Exception e) {
            System.out.format("error loading file '%s' from save directory '%s'%n", fullPath, saveDirectory);
            e.printStackTrace();
        }
        Path archiveFilePath = archiveDirectory.resolve(newFile.getFileName());
        try {
            Files.move(filePath, archiveFilePath);
        } catch (IOException e) {
            System.out.println("error moving file from " + filePath + " to " + archiveFilePath + ":");
            e.printStackTrace();
        }
    }
}