package com.teamquixote.ai;

import com.teamquixote.ai.actions.Action;
import com.teamquixote.ai.actions.WaitAction;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.items.Generator;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.InterlevelScene;
import com.watabou.pixeldungeon.scenes.StartScene;
import com.watabou.pixeldungeon.scenes.TitleScene;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.windows.WndMessage;
import com.watabou.pixeldungeon.windows.WndOptions;
import com.watabou.pixeldungeon.windows.WndSadGhost;
import com.watabou.pixeldungeon.windows.WndStory;
import com.watabou.utils.Bundle;
import com.watabou.utils.PDPlatformSupport;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class AiPixelDungeon extends PixelDungeon {
    private int turnsCount;
    private final AiAgent ai;
    private final AiPixelDungeonConfig config;

    private List<Bundle> previousStates = new ArrayList<>();

    public AiPixelDungeon(AiAgent ai) {
        this(ai, new AiPixelDungeonConfig());
    }

    public AiPixelDungeon(AiAgent ai, AiPixelDungeonConfig config) {
        super(config.LoadFromGameStateFile == null ? InterlevelScene.class : GameScene.class, new PDPlatformSupport("0.0", "", new AiInputProcessor()));

        this.ai = ai;
        this.config = config;
    }

    @Override
    public void create() {
        super.create();

        if (config.LoadFromGameStateFile != null) {
            loadGame(config.LoadFromGameStateFile);
        } else {
            InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
            StartScene.curClass = HeroClass.WARRIOR;
            Dungeon.init();
            Dungeon.chapters.clear();
        }
    }

    Bundle previousGameState, currentGameState, previousAction;

    Random random = new Random();

    @Override
    protected void update() {
        super.update();

        clearMessage();
        clearStory();
        clearChasm();
        clearSadGhost();

        if (heroIsAlive()) {
            if (canAct()) {
                currentGameState = getGameStateBundle();
                if (previousGameState != null) {
                    Bundle fullBundle = buildFullBundle(previousGameState, previousAction, currentGameState);
                    if(previousAction.getString("name") != "wait")
                        previousStates.add(fullBundle);
                    if (config.GameStateFileDirectory != null && previousAction != null) {
                        String fileName = String.format("%s/%s.json", config.GameStateFileDirectory, new Date().getTime());
                        try (OutputStream outputStream = new FileOutputStream(fileName)) {
                            Bundle.write(fullBundle, outputStream, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                GameState state = new GameState();
                previousGameState = currentGameState;
                Action a = ai.makeDecision(state);
                if(a.getClass() == WaitAction.class)
                    loadGame(previousStates.get(random.nextInt(previousStates.size())));
                previousAction = a.toBundle();
                a.execute();
                turnsCount++;
            }
        } else if (!previousStates.isEmpty())
            loadGame(previousStates.get(random.nextInt(previousStates.size())));
        else
            Game.instance.finish();
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
            RedButton rb = (RedButton)sadGhost.findFirstMember(RedButton.class);
            if(rb != null)
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
     * @return
     */
    private boolean heroIsAlive() {
        //todo: something about whether you have the thing that lets you resurrect
        return Dungeon.hero.isAlive();
    }

    private Bundle getGameStateBundle() {
        Bundle gameStateBundle = new Bundle();
        gameStateBundle.put("turnsCount", turnsCount);
        gameStateBundle.put("game", Dungeon.buildGameBundle());
        gameStateBundle.put("level", Dungeon.level);

        return gameStateBundle;
    }

    private Bundle buildFullBundle(Bundle previousGameState, Bundle action, Bundle resultingGameState) {
        Bundle bundle = new Bundle();
        bundle.put("s", previousGameState);
        bundle.put("a", action);
        bundle.put("s'", resultingGameState);

        return bundle;
    }

    private void loadGame(String fileName) {
        try (InputStream input = new FileInputStream(fileName)) {
            Bundle aiBundle = Bundle.read(input, false);
            loadGame(aiBundle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGame(Bundle aiBundle){
        Game.switchScene(TitleScene.class);
        Bundle gameStateBundle = aiBundle.getBundle("s'");
        InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
        Badges.loadGlobal();
        Generator.reset();
        Actor.fixTime();
        Dungeon.loadGame(gameStateBundle.getBundle("game"), gameStateBundle);
        Game.switchScene(GameScene.class);
    }
}