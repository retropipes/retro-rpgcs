/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.resourcemanagers;

import java.io.IOException;

import javax.imageio.ImageIO;

import com.puttysoftware.images.BufferedImageIcon;

public class BattleImageManager {
    private static final String DEFAULT_LOAD_PATH = "/com/puttysoftware/retrorpgcs/resources/graphics/objects/";
    private static String LOAD_PATH = BattleImageManager.DEFAULT_LOAD_PATH;
    private static Class<?> LOAD_CLASS = BattleImageManager.class;

    public static int getGraphicSize() {
        return 64;
    }

    /**
     *
     * @param name
     * @param baseID
     * @param transformColor
     * @return
     */
    public static BufferedImageIcon getImage(final String name,
            final int baseID, final int transformColor) {
        // Get it from the cache
        final var baseName = ObjectImageConstants.getObjectImageName(baseID);
        final var bii = BattleImageCache.getCachedImage(name,
                baseName);
        return ImageTransformer.templateTransformImage(bii, transformColor,
                BattleImageManager.getGraphicSize());
    }

    static BufferedImageIcon getUncachedImage(final String name) {
        try {
            final var normalName = ImageTransformer.normalizeName(name);
            final var url = BattleImageManager.LOAD_CLASS.getResource(
                    BattleImageManager.LOAD_PATH + normalName + ".png");
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
