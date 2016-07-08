package com.teamquixote.ai.gametree;

import com.teamquixote.ai.io.GameStateData;

import java.util.*;

public class GameStateTree {
    private Map<UUID, Node> statesById;
    private Map<UUID, List<UUID>> childIdsByParentId;
    private Set<UUID> leafStateIds;

    private class Node {
        final UUID id;
        final UUID parentId;
        final double value;

        private Node(UUID id, UUID parentId, double value) {
            this.id = id;
            this.parentId = parentId;
            this.value = value;
        }
    }

    public GameStateTree() {
        statesById = new HashMap<>();
        childIdsByParentId = new HashMap<>();
        leafStateIds = new HashSet<>();
    }

    private double calculateScore(GameStateData state) {
        return state.calculatePercentExplored();
    }

    private void propagate(Node node, Map<UUID, List<Double>> allScoresById, double score) {
        allScoresById.computeIfAbsent(node.id, k -> new ArrayList<>()).add(score);
        UUID parentId = node.parentId;
        if (parentId != null && statesById.containsKey(parentId))
            propagate(statesById.get(parentId), allScoresById, score);
    }

    private double getUCT(int wins, int plays, int totalPlays) {
        if (plays == 0)
            return 0;

        double coefficient = wins / (1.0 * plays);
        double sqrt2 = Math.sqrt(2.0);
        double sqrtFoo = Math.sqrt(Math.log(totalPlays));

        return coefficient * sqrt2 * sqrtFoo;
    }

    public void add(GameStateData gsd) {
        Node node = new Node(gsd.getId(), gsd.getParentId(), calculateScore(gsd));

        UUID id = node.id;
        if (!statesById.containsKey(id))
            statesById.put(id, node);

        if (childIdsByParentId.getOrDefault(id, new ArrayList<>()).size() == 0)
            leafStateIds.add(id);

        UUID parentId = node.parentId;
        if (parentId != null) {
            childIdsByParentId.computeIfAbsent(parentId, k -> new ArrayList<>()).add(id);
            leafStateIds.remove(parentId);
        }
    }

    public UUID chooseNextSimuation() {
        //keep track of all scores by a single node so I can calculate the total runs and total wins at each node
        Map<UUID, List<Double>> allScoresById = new HashMap<>();
        //using this measure to calculate the UCT
        int totalPlays = 0;
        //store all scores in descending order so I can easily pull off the top n% later on
        PriorityQueue<Double> scores = new PriorityQueue<>((Comparator<Double>) (o1, o2) -> (int) ((o2 - o1) * 1000));
        for (UUID id : leafStateIds) {
            Node node = statesById.getOrDefault(id, null);
            if (node != null) {
                UUID parentId = node.parentId;
                if (parentId != null) {
                    totalPlays++;
                    double score = node.value;
                    scores.add(score);
                    propagate(statesById.get(parentId), allScoresById, score);
                }
            }
        }

        int top = scores.size() / 20; //top 20% of scores are "wins"
        for (int i = 0; i < top; i++) scores.remove();
        final double winThreshold = scores.remove();

        double topUCT = Double.NEGATIVE_INFINITY;
        UUID topId = null;

        for (Map.Entry<UUID, List<Double>> e : allScoresById.entrySet()) {
            UUID id = e.getKey();
            int plays = e.getValue().size();
            int wins = (int) e.getValue().stream().filter(d -> d >= winThreshold).count();
            double uctScore = getUCT(wins, plays, totalPlays);
            if (uctScore > topUCT) {
                topUCT = uctScore;
                topId = id;
            }
        }

        return statesById.get(topId).id;
    }
}