package com.teamquixote.ai.statistics;

import com.teamquixote.ai.AiAgent;
import com.teamquixote.ai.GameState;

public interface GameStatistics {
    void onUpdate(AiAgent agent, GameState gameState);
}
