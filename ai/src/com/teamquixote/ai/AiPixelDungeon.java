package com.teamquixote.ai;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.input.GameAction;
import com.watabou.pixeldungeon.scenes.InterlevelScene;
import com.watabou.pixeldungeon.scenes.StartScene;
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

        if(canAct()) {
            AiGameState state = buildGameState();
            ai.update(state);
        }
    }

    private AiGameState buildGameState() {
        AiGameState state = new AiGameState();
        return state;
    }

    private boolean canAct()
    {
        return Dungeon.hero.ready;
    }
}
