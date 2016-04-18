package com.teamquixote.ai.heuristic;

import com.teamquixote.ai.heuristic.actions.Action;
import com.teamquixote.ai.heuristic.actions.WaitAction;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.InterlevelScene;
import com.watabou.pixeldungeon.scenes.StartScene;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.windows.WndMessage;
import com.watabou.pixeldungeon.windows.WndOptions;
import com.watabou.pixeldungeon.windows.WndSadGhost;
import com.watabou.pixeldungeon.windows.WndStory;
import com.watabou.utils.PDPlatformSupport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AiPixelDungeon extends PixelDungeon {
    private final AiAgent ai;
    private final Random random = new Random();

    private GameStateData currentStateData = new GameStateData();
    private List<GameStateData> previousStateData = new ArrayList<>();

    public AiPixelDungeon(AiAgent ai) {
        super(InterlevelScene.class, new PDPlatformSupport("0.0", "", new AiInputProcessor()));

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
        clearSadGhost();

        if (heroIsAlive()) {
            if (canAct()) {
                try {
                    Dungeon.saveAll();
                    final int max = 100;
                    if(previousStateData.size() > max)
                        previousStateData.remove(random.nextInt(max));
                    previousStateData.add(currentStateData.copy());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                GameState state = new GameState();
                Action a = ai.makeDecision(state);
                if(a.getClass() == WaitAction.class)
                    loadGame(previousStateData.get(random.nextInt(previousStateData.size())));
                a.execute();
            }
        }
        else if (!previousStateData.isEmpty())
            loadGame(previousStateData.get(random.nextInt(previousStateData.size())));
        else {
            Game.instance.finish();

            try {
                for (int i = 0; i < previousStateData.size(); i++)
                    currentStateData.saveToDisk("c:\\temp\\pd-saves\\currentStateData_" + i + ".json");
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void clearSadGhost() {
        WndSadGhost sadGhost = (WndSadGhost) scene.findFirstMember(WndSadGhost.class);
        if (sadGhost != null) {
            RedButton rb = (RedButton) sadGhost.findFirstMember(RedButton.class);
            if (rb != null)
                rb.click();
            else sadGhost.hide();
        }
    }

    private boolean canAct() {
        boolean isGameScene = scene.getClass().equals(GameScene.class);

        return Dungeon.hero.ready && scene.active && scene.alive && isGameScene;
    }

    /**
     * returns whether the hero is still alive
     *
     * @return
     */
    private boolean heroIsAlive() {
        //todo: something about whether you have the thing that lets you resurrect
        return Dungeon.hero.isAlive();
    }

    private void loadGame(GameStateData gameState) {
        currentStateData = gameState;
        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
        Game.switchScene(InterlevelScene.class);
    }

    @Override
    public InputStream openFileInput(String fileName) throws IOException {
        if (currentStateData == null)
            throw new IOException("File " + fileName + " doesn't exist");
        return currentStateData.loadSection(fileName);
    }

    @Override
    public OutputStream openFileOutput(String fileName) {
        if (currentStateData == null)
            return null;

        return currentStateData.saveSection(fileName);
    }
}