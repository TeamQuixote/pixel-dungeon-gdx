package com.teamquixote.ai;


import com.watabou.input.NoosaInputProcessor;
import com.watabou.pixeldungeon.input.GameAction;

public class AiInputProcessor extends NoosaInputProcessor<GameAction> {


    @Override
    protected GameAction keycodeToGameAction(int keycode) {
        return AiKeyCodes.getInstance().getAction(keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
}
