package com.teamquixote.ai.actions;

import com.teamquixote.ai.io.GameStateData;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Action {
    public abstract String getType();

    public abstract void execute(GameStateData gameStateData);

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", getType());

        return jsonObject;
    }

    public static List<Action> getValidActions(GameStateData gameStateData) {
        List<Action> validActions = new ArrayList<>();

        int heroPos = gameStateData.getHeroPosition();

        //if on an exit, there aren't any moves you can do until the next level loads
        if (gameStateData.isPositionExit(heroPos))
            return validActions;

        for (int adj : GameStateData.Utilities.getAdjacent(heroPos))
            if (gameStateData.isPositionPassable(adj))
                validActions.add(new Move(GameStateData.Utilities.getDx(heroPos, adj), GameStateData.Utilities.getDy(heroPos, adj)));

        return validActions;
    }
}