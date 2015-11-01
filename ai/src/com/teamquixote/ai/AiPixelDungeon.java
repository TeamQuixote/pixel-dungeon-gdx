package com.teamquixote.ai;

import com.teamquixote.ai.actions.Action;
import com.teamquixote.ai.statistics.GameStatistics;
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
    private final GameStatistics gameStatistics;

    public AiPixelDungeon(PDPlatformSupport<GameAction> platformSupport, AiAgent ai, GameStatistics gameStatistics) {
        super(InterlevelScene.class, platformSupport);

        this.ai = ai;
        this.gameStatistics = gameStatistics;
    }

    @Override
    public void create() {
        super.create();

        InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
        StartScene.curClass = HeroClass.WARRIOR;
        Dungeon.init();
        Dungeon.chapters.clear();
    }

    @Override
    protected void update() {
        super.update();

        clearMessage();
        clearStory();
        clearChasm();

        if (canAct()) {
            GameState state = new GameState();
            if (gameStatistics != null)
                gameStatistics.onUpdate(ai, state);
            Action a = ai.makeDecision(state);
            a.execute();
        }
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

        return Dungeon.hero.ready && scene.active && scene.alive && isGameScene;
    }
}