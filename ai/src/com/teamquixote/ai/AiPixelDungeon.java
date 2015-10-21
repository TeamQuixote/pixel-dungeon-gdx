package com.teamquixote.ai;

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
    private AiAgent ai;

    public AiPixelDungeon(PDPlatformSupport<GameAction> platformSupport, AiAgent ai) {
        super(InterlevelScene.class, platformSupport);

        this.ai = ai;
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

        if(canAct()) {
            AiGameState state = buildGameState();
            ai.update(state);
        }
    }

    private AiGameState buildGameState() {
        AiGameState state = new AiGameState();
        return state;
    }

    private void clearChasm(){
        WndOptions options = (WndOptions)scene.findFirstMember(WndOptions.class);
        if(options != null) {
            options.select(2);
        }
    }

    private void clearStory() {
        WndStory story = (WndStory)scene.findFirstMember(WndStory.class);
        if(story != null)
            story.hide();
    }

    private void clearMessage() {
        WndMessage msg = (WndMessage)scene.findFirstMember(WndMessage.class);
        if(msg != null)
            msg.hide();
    }

    private boolean canAct() {
        boolean isGameScene = scene.getClass().equals(GameScene.class);

        return Dungeon.hero.ready && scene.active && scene.alive && isGameScene;
    }
}
