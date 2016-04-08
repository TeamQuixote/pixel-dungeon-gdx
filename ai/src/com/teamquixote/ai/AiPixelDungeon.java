package com.teamquixote.ai;

import com.teamquixote.ai.actions.Action;
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
import com.watabou.pixeldungeon.windows.WndMessage;
import com.watabou.pixeldungeon.windows.WndOptions;
import com.watabou.pixeldungeon.windows.WndStory;
import com.watabou.utils.Bundle;
import com.watabou.utils.PDPlatformSupport;

import java.io.*;
import java.util.Date;

public class AiPixelDungeon extends PixelDungeon {
    private int turnsCount;
    private final AiAgent ai;
    private final AiPixelDungeonConfig config;

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
            try (InputStream input = new FileInputStream(config.LoadFromGameStateFile)) {
                Bundle aiBundle = Bundle.read(input, false);
                Bundle gameStateBundle = aiBundle.getBundle("s'");
                Badges.loadGlobal();
                Generator.reset();
                Actor.fixTime();
                Dungeon.loadGame(gameStateBundle.getBundle("game"), gameStateBundle);
                Game.switchScene(GameScene.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
            StartScene.curClass = HeroClass.WARRIOR;
            Dungeon.init();
            Dungeon.chapters.clear();
        }
    }

    Bundle previousGameState, currentGameState, previousAction;

    @Override
    protected void update() {
        super.update();

        clearMessage();
        clearStory();
        clearChasm();

        if (heroIsAlive()) {
            if (canAct()) {
                currentGameState = getGameStateBundle();
                if (config.GameStateFileDirectory != null && previousGameState != null && previousAction != null) {
                    String fileName = String.format("%s/%s.json", config.GameStateFileDirectory, new Date().getTime());
                    try (OutputStream outputStream = new FileOutputStream(fileName)) {
                        Bundle.write(buildFullBundle(previousGameState, previousAction, currentGameState), outputStream, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                GameState state = new GameState();
                previousGameState = currentGameState;
                Action a = ai.makeDecision(state);
                previousAction = a.toBundle();
                a.execute();
                turnsCount++;
            }
        } else
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
}