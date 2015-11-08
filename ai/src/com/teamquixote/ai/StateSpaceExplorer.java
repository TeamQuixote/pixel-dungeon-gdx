package com.teamquixote.ai;

import com.teamquixote.ai.actions.Action;

import java.util.*;

public class StateSpaceExplorer {
    private final GameStateUtility utilityFunction;

    public StateSpaceExplorer(GameStateUtility utilityFunction) {
        this.utilityFunction = utilityFunction;
    }

    public GameState findBestState(GameState start) {
        Set<GameState> explored = new HashSet<>();
        Queue<StateCostPair> open = new PriorityQueue<>();
        Map<GameState, Double> costs = new HashMap<>();

        open.add(new StateCostPair(start));
        costs.put(start, 0.0);

        while (!open.isEmpty()) {
            StateCostPair p = open.remove();
            GameState current = p.state;

            if (current.previousAction != null && utilityFunction.isTerminalState(current)) {
                return current;
            }

            if (!explored.contains(current))
                explored.add(current);
            for (Action a : current.getActions()) {
                StateCostPair fringe = new StateCostPair(new GameState(current, a));

                double edgeCost = a.getActionCost(current);
                double tentativeCost = costs.get(current) + edgeCost;
                double currentCost = costs.getOrDefault(fringe.state, Double.POSITIVE_INFINITY);

                //if the cost of this path is lower than the current path (or no current path exists), add it to the
                // queue of fringe states
                if (tentativeCost < currentCost) {
                    costs.put(fringe.state, tentativeCost);
                    //if this state is already in our priority queue, ideally we'd just update the priority.
                    // However, this is only possible by removing and adding the item again, which is a O(n)
                    // operation.  Instead, we'll just add a duplicate item and skip exploring it if it comes up
                    // again
                    open.add(fringe);
                }
            }
        }

        //no goal state found
        return null;
    }

    private class StateCostPair implements Comparable<StateCostPair> {
        public final GameState state;
        public final double utility;

        public StateCostPair(GameState state) {
            this(state, utilityFunction.getUtility(state));
        }

        public StateCostPair(GameState state, double utility) {
            this.state = state;
            this.utility = utility;
        }

        @Override
        public int compareTo(StateCostPair o) {
            if(utility > o.utility) return 1;
            if(utility < o.utility) return -1;
            return 0;
        }
    }
}