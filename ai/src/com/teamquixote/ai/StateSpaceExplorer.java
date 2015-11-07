package com.teamquixote.ai;

        import java.util.*;
        import java.util.stream.Collectors;

public class StateSpaceExplorer {
    private final GameStateUtility utilityFunction;

    public StateSpaceExplorer(GameStateUtility utilityFunction) {
        this.utilityFunction = utilityFunction;
    }

    public GameState findBestState(GameState state) {
        Queue<StateUtilityPair> states = new PriorityQueue<>();
        Set<GameState> exploredStates = new HashSet<>();
        states.addAll(state.getActions().stream()
                .map(a -> new StateUtilityPair(new GameState(state, a)))
                .collect(Collectors.toList()));

        GameState current = null;
        while (!states.isEmpty() && !utilityFunction.isTerminalState((current = states.remove().state))) {
            exploredStates.add(current);
            final GameState finalCurrent = current;
            for (GameState sPrime : current.getActions().stream().map(a -> new GameState(finalCurrent, a)).collect(Collectors.toList()))
                if (!exploredStates.contains(sPrime)) states.add(new StateUtilityPair(sPrime));
        }

        return current;
    }

    private class StateUtilityPair implements Comparable<StateUtilityPair> {
        public final GameState state;
        public final double utility;

        public StateUtilityPair(GameState state) {
            this(state, utilityFunction.getUtility(state));
        }

        public StateUtilityPair(GameState state, double utility) {
            this.state = state;
            this.utility = utility;
        }

        @Override
        public int compareTo(StateUtilityPair o) {
            if(utility > o.utility) return 1;
            if(utility < o.utility) return -1;
            return 0;
        }
    }
}