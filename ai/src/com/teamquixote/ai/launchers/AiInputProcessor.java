package com.teamquixote.ai.launchers;

import com.watabou.input.NoosaInputProcessor;

public class AiInputProcessor extends NoosaInputProcessor {

    @Override
    protected Object keycodeToGameAction(int keycode) {
        return null;
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