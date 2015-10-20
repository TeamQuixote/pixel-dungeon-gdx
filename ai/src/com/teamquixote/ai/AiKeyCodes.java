package com.teamquixote.ai;

import com.watabou.pixeldungeon.input.GameAction;

import java.util.ArrayList;
import java.util.List;

public class AiKeyCodes {

    private static AiKeyCodes instance;
    public static AiKeyCodes getInstance(){
        if(instance == null){
            instance = new AiKeyCodes();
        }
        return instance;
    }


    private List<GameAction> keyToActionSet = new ArrayList<>();

    public AiKeyCodes() {
        for(GameAction action : GameAction.values())
            keyToActionSet.add(action);
    }

    public int getActionKey(GameAction action) {
        return keyToActionSet.indexOf(action);
    }

    public GameAction getAction(int key){
        if(key > keyToActionSet.size())
            return GameAction.REST;

        return keyToActionSet.get(key);
    }
}
