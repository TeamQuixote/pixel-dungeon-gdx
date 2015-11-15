package com.teamquixote.ai;

import com.teamquixote.ai.actions.Action;
import com.teamquixote.ai.agents.AngryFrontiersman;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.items.Generator;
import com.watabou.pixeldungeon.levels.Level;
import org.junit.Test;

public class BasicTest {
    @Test
    public void test(){

        Dungeon d = new Dungeon();
        Dungeon.setInstance(d);
        d.init(0, HeroClass.WARRIOR);
        Generator.reset();
        Level level = d.newLevel();
        d.switchLevel(level, level.entrance);


        AiAgent agent = new AngryFrontiersman();

        for(int i = 0; i < 20; i++) {
            d.process();
            GameState g = new GameState(d);
            Action action = agent.makeDecision(g);
            action.execute();
            System.out.println(d.hero.pos);
        }

    }
}
