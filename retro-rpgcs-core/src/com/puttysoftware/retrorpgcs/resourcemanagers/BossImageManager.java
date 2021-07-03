/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.resourcemanagers;

import java.io.IOException;

import javax.imageio.ImageIO;

import com.puttysoftware.images.BufferedImageIcon;

public class BossImageManager {
    private static final String DEFAULT_LOAD_PATH = "/com/puttysoftware/retrorpgcs/resources/graphics/boss/";
    private static String LOAD_PATH = BossImageManager.DEFAULT_LOAD_PATH;
    private static Class<?> LOAD_CLASS = BossImageManager.class;
    static int BOSS_IMAGE_SIZE = 64;

    public static BufferedImageIcon getBossImage() {
        // Get it from the cache
        final var bii = BossImageCache.getCachedImage("boss");
        return ImageTransformer.getTransformedImage(bii,
                BossImageManager.BOSS_IMAGE_SIZE);
    }

    static BufferedImageIcon getUncachedImage(final String name) {
        try {
            final var normalName = ImageTransformer.normalizeName(name);
            final var url = BossImageManager.LOAD_CLASS.getResource(
                    BossImageManager.LOAD_PATH + normalName + ".png");
            final var image = ImageIO.read(url);
            return new BufferedImageIcon(image);
        } catch (final IOException ie) {
            return null;
        } catch (final NullPointerException np) {
            return null;
        } catch (final IllegalArgumentException ia) {
            return null;
        }
    }
}
