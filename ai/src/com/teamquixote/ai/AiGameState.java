package com.teamquixote.ai;

public class AiGameState {
    private boolean canAct = false;

    public void setCanAct(boolean value)
    {
        canAct = value;
    }

    public boolean getCanAct()
    {
        return canAct;
    }
}
