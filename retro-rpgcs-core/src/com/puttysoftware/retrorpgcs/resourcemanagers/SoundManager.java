/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.resourcemanagers;

import com.puttysoftware.diane.loaders.SoundLoader;

public class SoundManager {
    @Obsolete
    public static void playSound(final int soundID) {
        SoundLoader.play(null);
    }
}