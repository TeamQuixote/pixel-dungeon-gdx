package com.teamquixote.ai;

import com.teamquixote.ai.actions.Action;
import com.teamquixote.ai.agents.Spelunker;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.hero.HeroAction;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.items.Generator;
import com.watabou.pixeldungeon.levels.Level;
import org.junit.Test;

public class BasicTest {

    @Test
    public void test(){

        Dungeon d = new Dungeon();
        DungeonStats stats = new DungeonStats(d);
        Dungeon.setInstance(d);
        d.init(0, HeroClass.WARRIOR);
        d.chapters.clear();
        Generator.reset();
        Level level = d.newLevel();
        d.switchLevel(level, level.entrance);

        AiAgent agent = new Spelunker();
        float step = 1;

        for(int i = 0; i < 4000; i++) {
            Game.elapsed = Game.timeScale * step * 0.001f;
            d.process();

            if(d.hero.ready) {
                GameState g = new GameState(d);
                Action action = agent.makeDecision(g);
                describeState(stats, action, d);
                action.execute();
            }

            if(d.hero.curAction instanceof HeroAction.Descend){
                System.out.print("Descending...");
                break;
            }
        }
    }

    private void describeState(DungeonStats stats, Action action, Dungeon dungeon) {
        System.out.format("Dungeon Time: %f\n", dungeon.now);
        System.out.format("Hero Location: %s\n", posToRowColumn(dungeon.hero.pos));
        System.out.format("Discovered Tiles: %d\n", stats.getTotalDiscoveredTiles());
        System.out.format("Action: %s\n", action.describeAction());
        System.out.print("\n");
    }

    private String posToRowColumn(int pos){
        return String.format("%d,%d",DungeonMap.MapUtilities.getColumn(pos), DungeonMap.MapUtilities.getRow(pos));
    }
}
