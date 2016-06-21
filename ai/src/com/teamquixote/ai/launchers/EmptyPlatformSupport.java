package com.teamquixote.ai.launchers;

import com.watabou.utils.PDPlatformSupport;

/**
 * dumb filler class to meet the PixelDungeon api requirement
 */
public class EmptyPlatformSupport extends PDPlatformSupport {
    public EmptyPlatformSupport() {
        super("", "", new AiInputProcessor());
    }
}
