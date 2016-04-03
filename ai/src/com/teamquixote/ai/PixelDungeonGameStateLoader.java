package com.teamquixote.ai;

public class PixelDungeonGameStateLoader {
    public static void main(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("args must contain single argument: the file to load");
        String loadFileName = args[0];

        AiLauncher.launch(new AiPixelDungeonConfig(null, loadFileName));
    }
}
