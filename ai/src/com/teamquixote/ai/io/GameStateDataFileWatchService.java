package com.teamquixote.ai.io;

import com.teamquixote.ai.gametree.GameStateTree;
import com.teamquixote.ai.simulators.SimulatorPool;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Crude MVP for a means of listening to a folder and processing files as they come in
 */
public class GameStateDataFileWatchService {
    private GameStateTree tree;
    private SimulatorPool simulatorPool;
    private Path saveDirectory, archiveDirectory;
    private Path treeArchivePath, treeArchiveBackupPath;

    private int totalFilesRead = 0;
    private long totalFileProcessTime = 0;

    public GameStateDataFileWatchService(String saveDirectory) throws IOException {
        this.saveDirectory = Paths.get(saveDirectory);
        this.archiveDirectory = this.saveDirectory.resolve("_archive");
        this.treeArchivePath = this.archiveDirectory.resolve("_tree.ser");
        this.treeArchiveBackupPath = this.archiveDirectory.resolve("_tree.ser.bak");
        this.tree = readArchiveTree();
        if (this.tree == null) {
            System.out.println("no archive tree found, reading entire archive...");
            this.tree = new GameStateTree();
            processArchives();
        } else {
            System.out.println("archive found!");
        }
        this.simulatorPool = new SimulatorPool(Paths.get("C:\\development\\pixel-dungeon-gdx\\ai\\build\\libs\\ai-1.7.2a-1.jar"),
                this.saveDirectory);

        System.out.println("save directory: " + saveDirectory);
        System.out.println("archive directory: " + archiveDirectory);
        writeArchiveTree();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logStats();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                writeArchiveTree();
                processFiles();
                simulatorPool.start(chooseNextSimulation(), 25);
            }
        }).start();
    }

    private void logStats() {
        String currentTime = LocalDateTime.now().toString();
        String filesRead = "Files read: " + totalFilesRead;

        double duration = totalFileProcessTime / 1000.0;
        String filesPerSec = duration == 0 ? "" : ("Files/sec: " + totalFilesRead / duration);

        String avgSimulations = "Avg. Sim. Duration: " + simulatorPool.statistics.getAvgSimulationDuration();

        String bestScore = "", winThreshold = "", totalStates = "";
        if (tree.statistics != null) {
            bestScore = "Best score: " + tree.statistics.bestScore;
            winThreshold = "Win threshold: " + tree.statistics.winThreshold;
            totalStates = "Total states: " + tree.statistics.size;
        }


        System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s\n", currentTime, filesRead, filesPerSec, avgSimulations, totalStates, bestScore, winThreshold);
    }

    private void processFiles() {
        processFiles(saveDirectory);
    }

    private void processArchives() {
        processFiles(archiveDirectory);
    }

    private void writeArchiveTree() {
        if (Files.exists(treeArchivePath))
            try {
                Files.move(treeArchivePath, treeArchiveBackupPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

        try (ObjectOutputStream str = new ObjectOutputStream(new FileOutputStream(treeArchivePath.toAbsolutePath().toString()))) {
            str.writeObject(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GameStateTree readArchiveTree() {
        if (!Files.exists(treeArchivePath))
            return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(treeArchivePath.toAbsolutePath().toString()))) {
            return (GameStateTree) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    private void processFiles(Path readPath) {
        try {
            Files.list(readPath)
                    .filter(Files::isRegularFile)
                    .forEach(p -> {
                        processFile(readPath.resolve(p));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFile(Path filePath) {
        Instant start = Instant.now();

        try {
            GameStateData gsd = GameStateData.loadFromDisk(filePath);
            tree.add(gsd);

            totalFilesRead++;
            Instant end = Instant.now();
            totalFileProcessTime += Duration.between(start, end).toMillis();
        } catch (Exception e) {
            System.out.format("error loading file '%s' from save directory '%s'%n", filePath.getFileName(), saveDirectory);
            e.printStackTrace();
        }
        if (!filePath.startsWith(archiveDirectory)) {
            Path archiveFilePath = archiveDirectory.resolve(filePath.getFileName());
            try {
                if (Files.exists(archiveFilePath))
                    Files.delete(filePath);
                else
                    Files.move(filePath, archiveFilePath);
            } catch (IOException e) {
                System.out.println("error moving file from " + filePath + " to " + archiveFilePath + ":");
                e.printStackTrace();
            }
        }
    }

    private Path chooseNextSimulation() {
        UUID nextSim = tree.chooseNextSimuation();
        if (nextSim == null)
            return null;

        return archiveDirectory.resolve(nextSim.toString());
    }
}