package com.teamquixote.ai;

import com.teamquixote.ai.actions.Action;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.input.GameAction;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.InterlevelScene;
import com.watabou.pixeldungeon.scenes.StartScene;
import com.watabou.pixeldungeon.windows.WndMessage;
import com.watabou.pixeldungeon.windows.WndOptions;
import com.watabou.pixeldungeon.windows.WndStory;
import com.watabou.utils.PDPlatformSupport;

public class AiPixelDungeon extends PixelDungeon {
    private final AiAgent ai;

    public AiPixelDungeon(PDPlatformSupport<GameAction> platformSupport, AiAgent ai) {
        super(InterlevelScene.class, platformSupport);

        this.ai = ai;
    }

    @Override
    public void create() {
        super.create();

        InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
        Dungeon.getInstance().init(PixelDungeon.challenges(), HeroClass.WARRIOR);
        Dungeon.getInstance().chapters.clear();
    }

    @Override
    protected void update() {
        super.update();

        clearMessage();
        clearStory();
        clearChasm();

        if (canAct()) {
            GameState state = new GameState(Dungeon.getInstance());
            Action a = ai.makeDecision(state);
            describeState(a, Dungeon.getInstance());
            a.execute();
        }
    }

    private void describeState(Action action, Dungeon dungeon) {
        DungeonStats stats = new DungeonStats(dungeon);
        System.out.format("Dungeon Time: %f\n", dungeon.now);
        System.out.format("Hero Location: %s\n", posToRowColumn(dungeon.hero.pos));
        System.out.format("Discovered Tiles: %d\n", stats.getTotalDiscoveredTiles());
        System.out.format("Action: %s\n", action.describeAction());
        System.out.print("\n");
    }

    private String posToRowColumn(int pos){
        return String.format("%d,%d",DungeonMap.MapUtilities.getColumn(pos), DungeonMap.MapUtilities.getRow(pos));
    }

    private void clearChasm() {
        WndOptions options = (WndOptions) scene.findFirstMember(WndOptions.class);
        if (options != null) {
            options.select(2);
        }
    }

    private void clearStory() {
        WndStory story = (WndStory) scene.findFirstMember(WndStory.class);
        if (story != null)
            story.hide();
    }

    private void clearMessage() {
        WndMessage msg = (WndMessage) scene.findFirstMember(WndMessage.class);
        if (msg != null)
            msg.hide();
    }

    private boolean canAct() {
        boolean isGameScene = scene.getClass().equals(GameScene.class);

        return Dungeon.getInstance().hero.ready && scene.active && scene.alive && isGameScene;
    }
}