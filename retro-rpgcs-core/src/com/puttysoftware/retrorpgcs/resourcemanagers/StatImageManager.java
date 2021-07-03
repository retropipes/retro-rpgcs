/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.resourcemanagers;

import java.io.IOException;

import javax.imageio.ImageIO;

import com.puttysoftware.images.BufferedImageIcon;

public class StatImageManager {
    private static final String DEFAULT_LOAD_PATH = "/com/puttysoftware/retrorpgcs/resources/graphics/stats/";
    private static String LOAD_PATH = StatImageManager.DEFAULT_LOAD_PATH;
    private static Class<?> LOAD_CLASS = StatImageManager.class;

    public static BufferedImageIcon getImage(final int imageID) {
        // Get it from the cache
        final var name = StatImageConstants.getStatImageName(imageID);
        return StatImageCache.getCachedImage(name);
    }

    static BufferedImageIcon getUncachedImage(final String name) {
        try {
            final var normalName = ImageTransformer.normalizeName(name);
            final var url = StatImageManager.LOAD_CLASS.getResource(
                    StatImageManager.LOAD_PATH + normalName + ".png");
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
