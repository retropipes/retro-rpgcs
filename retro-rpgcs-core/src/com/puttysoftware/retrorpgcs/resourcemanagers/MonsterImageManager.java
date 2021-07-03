/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.resourcemanagers;

import java.io.IOException;

import javax.imageio.ImageIO;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.retrorpgcs.creatures.monsters.Element;

public class MonsterImageManager {
    private static final String DEFAULT_LOAD_PATH = "/com/puttysoftware/retrorpgcs/resources/graphics/monsters/";
    private static String LOAD_PATH = MonsterImageManager.DEFAULT_LOAD_PATH;
    private static Class<?> LOAD_CLASS = MonsterImageManager.class;
    static int MONSTER_IMAGE_SIZE = 64;

    public static BufferedImageIcon getImage(final String name,
            final Element e) {
        // Get it from the cache
        final var bii = MonsterImageCache.getCachedImage(name,
                e.getFaith().getColor().getRGB());
        return ImageTransformer.getTransformedImage(bii,
                MonsterImageManager.MONSTER_IMAGE_SIZE);
    }

    static BufferedImageIcon getUncachedImage(final String name) {
        try {
            final var normalName = ImageTransformer.normalizeName(name);
            final var url = MonsterImageManager.LOAD_CLASS.getResource(
                    MonsterImageManager.LOAD_PATH + normalName + ".png");
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
