package com.teamquixote.ai.agents;

import com.teamquixote.ai.*;
import com.watabou.input.NoosaInputProcessor;
import com.watabou.pixeldungeon.input.GameAction;

public class WallFollowingAgent extends AiAgent{

    private Direction currentDirection = Direction.North;
    private boolean foundWall = false;

    public WallFollowingAgent(NoosaInputProcessor processor) {
        super(processor);
    }

    @Override
    protected void makeDecision(AiGameState state) {

        if(!foundWall){
            if(!state.getRelativeTerrain(currentDirection, RelativeDirection.Forward).isPassable()){
                foundWall = true;
            } else {
                doAction(toGameAction(currentDirection));
                return;
            }
        }

        if(isPassibleOrDoor(state.getRelativeTerrain(currentDirection, RelativeDirection.Forward))) {
            if (isPassibleOrDoor(state.getRelativeTerrain(currentDirection, RelativeDirection.Right))) {
                currentDirection = Direction.getRelativeDirection(currentDirection, RelativeDirection.Right);
            }
        } else {
            if (isPassibleOrDoor(state.getRelativeTerrain(currentDirection, RelativeDirection.Right)) &&
                    !isPassibleOrDoor(state.getRelativeTerrain(currentDirection, RelativeDirection.BackRight))) {
                currentDirection = Direction.getRelativeDirection(currentDirection, RelativeDirection.Right);
            } else {
                currentDirection = Direction.getRelativeDirection(currentDirection, RelativeDirection.Left);
            }
        }
        doAction(toGameAction(currentDirection));
    }

    private boolean isPassibleOrDoor(TerrainInfo info){
        return info.isPassable() || info.isDoor();
    }

    private GameAction toGameAction(Direction dir){
        switch(dir){
            case North:
                return GameAction.MOVE_UP;
            case West:
                return GameAction.MOVE_RIGHT;
            case South:
                return GameAction.MOVE_DOWN;
            default:
                return GameAction.MOVE_LEFT;
        }
    }
}
