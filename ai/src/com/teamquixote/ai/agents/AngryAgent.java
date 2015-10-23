package com.teamquixote.ai.agents;

import com.teamquixote.ai.AiGameState;
import com.teamquixote.ai.TerrainInfo;
import com.watabou.input.NoosaInputProcessor;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.input.GameAction;
import com.watabou.pixeldungeon.levels.Level;

import java.util.List;

/**
 * This agent hates rats.
 */
public class AngryAgent extends WallFollowingAgent {
    public AngryAgent(NoosaInputProcessor processor) {
        super(processor);
    }

    @Override
    protected void makeDecision(AiGameState state) {
        Integer closestMobLocation = getClosestPosition(state.getHeroPosition(), state.getMobLocations());
        if (closestMobLocation != null) {
            GameAction moveTowards = getGameAction(state.getHeroPosition(), closestMobLocation);
            if (moveTowards != null) {
                int nextPosition = getNextPosition(state.getHeroPosition(), moveTowards);
                boolean isPassable = new TerrainInfo(Dungeon.level.map[nextPosition]).isPassable();
                if (isPassable) {
                    doAction(moveTowards);
                    return;
                }
            }
        }

        super.makeDecision(state);
    }

    int getColumn(int position) {
        return position % Level.WIDTH;
    }

    int getRow(int position) {
        return position / Level.WIDTH;
    }

    /**
     * gets the game action that points towards the destination from the source
     *
     * @param source
     * @param destination
     * @return
     */
    private GameAction getGameAction(int source, int destination) {
        int sourceColumn = getColumn(source);
        int destinationColumn = getColumn(destination);
        int sourceRow = getRow(source);
        int destinationRow = getRow(destination);

        if (sourceColumn == destinationColumn) {
            if (sourceRow < destinationRow)
                return GameAction.MOVE_DOWN;
            if (destinationRow < sourceRow)
                return GameAction.MOVE_UP;
        }
        if (sourceColumn < destinationColumn) {
            if (sourceRow < destinationRow)
                return GameAction.MOVE_BOTTOM_RIGHT;
            if (sourceRow > destinationRow)
                return GameAction.MOVE_TOP_RIGHT;
            return GameAction.MOVE_RIGHT;
        }
        if (sourceColumn > destinationColumn) {
            if (sourceRow < destinationRow)
                return GameAction.MOVE_BOTTOM_LEFT;
            if (sourceRow > destinationRow)
                return GameAction.MOVE_TOP_LEFT;
            return GameAction.MOVE_LEFT;
        }

        //this shouldn't happen, but if for some reason we share a tile with the destination, just return null
        return null;
    }

    private int getNextPosition(int location, GameAction action) {
        int newPosition = location;
        if(action == GameAction.MOVE_UP || action == GameAction.MOVE_TOP_RIGHT || action == GameAction.MOVE_TOP_LEFT)
                newPosition -= Level.WIDTH;

        if(action == GameAction.MOVE_DOWN || action == GameAction.MOVE_BOTTOM_RIGHT || action == GameAction.MOVE_BOTTOM_LEFT)
                newPosition += Level.WIDTH;

        if(action == GameAction.MOVE_TOP_RIGHT || action == GameAction.MOVE_RIGHT || action == GameAction.MOVE_BOTTOM_RIGHT)
                newPosition++;
        if(action == GameAction.MOVE_TOP_LEFT || action == GameAction.MOVE_LEFT || action == GameAction.MOVE_BOTTOM_LEFT)
                newPosition--;

        return newPosition;
    }

    private Integer getClosestPosition(int position, List<Integer> positions) {
        Integer closestPosition = null;
        double closestDistance = Double.POSITIVE_INFINITY;

        for (Integer i : positions) {
            double distance = getCartesianDistance(position, i);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPosition = i;
            }
        }

        if(closestPosition != null)
            System.out.println("closest enemy is " + closestDistance);

        return closestPosition;
    }

    private double getCartesianDistance(int a, int b) {

        int dx = getColumn(a) - getColumn(b);
        int dy = getRow(a) - getRow(b);

        return Math.sqrt(dx * dx + dy + dy);
    }
}