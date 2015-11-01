package com.teamquixote.ai;

        import java.util.HashSet;
        import java.util.LinkedList;
        import java.util.Queue;
        import java.util.Set;
        import java.util.stream.Collectors;

public class StateSpaceExplorer {
    private final TerminalStateConditions terminalStateConditions;

    public StateSpaceExplorer(TerminalStateConditions terminalStateConditions) {
        this.terminalStateConditions = terminalStateConditions;
    }


    public GameState findBestState(GameState state) {
        Queue<GameState> states = new LinkedList<>();
        Set<GameState> exploredStates = new HashSet<>();
        states.addAll(state.getActions().stream().map(a -> new GameState(state, a)).collect(Collectors.toList()));

        GameState current = null;
        while (!states.isEmpty() && !terminalStateConditions.isTerminalState((current = states.remove()))) {
            exploredStates.add(current);
            final GameState finalCurrent = current;
            for (GameState sPrime : current.getActions().stream().map(a -> new GameState(finalCurrent, a)).collect(Collectors.toList()))
                if (!exploredStates.contains(sPrime)) states.add(sPrime);
        }

        return current;
    }
}