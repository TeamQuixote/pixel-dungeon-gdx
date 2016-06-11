package com.teamquixote.ai.dungeons;

import com.teamquixote.ai.actions.Action;
import com.teamquixote.ai.agents.AiAgent;
import com.teamquixote.ai.io.GameStateData;
import com.watabou.noosa.Game;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AiPixelDungeon extends PixelDungeon {
    protected final AiAgent ai;

    private GameStateData currentState = new GameStateData();
    private static final boolean showPerformanceStats = true;
    Long startTime;
    int totalActionsPlayed = 0;

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

        if (Dungeon.hero.isAlive()) {
            if (canAct()) {
                if (startTime == null)
                    startTime = System.currentTimeMillis();

                try {
                    Dungeon.saveAll();
                    stateChanged(currentState.copy());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Action a = ai.makeDecision(currentState);
                currentState = currentState.createChild(a);
                a.execute(currentState);

                totalActionsPlayed++;
                long elapsed = System.currentTimeMillis() - startTime;
                if (showPerformanceStats) {
                    System.out.println("Total actions: " + totalActionsPlayed);
                    System.out.println("Actions per second: " + (1000.0 * totalActionsPlayed) / elapsed);
                    System.out.println("Elapsed time: " + (elapsed / 1000.0));
                }
            }
        } else {
            heroDied();
            Game.instance.finish();
        }
    }

    protected void stateChanged(GameStateData state){}

    protected void heroDied(){ }

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

    @Override
    public InputStream openFileInput(String fileName) throws IOException {
        if (currentState == null)
            throw new IOException("File " + fileName + " does not exist");
        return currentState.loadSection(fileName);
    }

    @Override
    public OutputStream openFileOutput(String fileName) {
        if (currentState == null)
            return null;
        return currentState.saveSection(fileName);
    }
}