package com.teamquixote.ai.actions;

import com.teamquixote.ai.io.GameStateData;
import com.watabou.pixeldungeon.Dungeon;
import org.json.JSONObject;

public class Move extends Action {
    /**
     * number of tiles offset relative to the hero's position (left and up are negative)
     */
    public final int dX, dY;

    public Move(int dX, int dY) {
        this.dX = dX;
        this.dY = dY;
    }

    @Override
    public String getType() {
        return "move";
    }

    @Override
    public void execute(GameStateData gameStateData) {
        int target = gameStateData.getHeroPosition() + dX + (dY*GameStateData.Utilities.DUNGEON_WIDTH);
        //stole this from GameScene.defaultCellListener
        if (Dungeon.hero.handle(target)) {
            Dungeon.hero.next();
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = super.toJSON();
        jsonObject.put("dX", dX);
        jsonObject.put("dY", dY);

        return jsonObject;
    }
}