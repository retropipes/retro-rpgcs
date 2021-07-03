/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.resourcemanagers;

import com.puttysoftware.images.BufferedImageIcon;

public class StatImageCache {
    // Fields
    private static CacheEntry[] cache;
    private static int CACHE_INCREMENT = 20;
    private static int CACHE_SIZE = 0;

    static synchronized void addToCache(final String name,
            final BufferedImageIcon bii) {
        if (StatImageCache.cache == null) {
            StatImageCache.cache = new CacheEntry[StatImageCache.CACHE_INCREMENT];
        }
        if (StatImageCache.CACHE_SIZE == StatImageCache.cache.length) {
            StatImageCache.expandCache();
        }
        StatImageCache.cache[StatImageCache.CACHE_SIZE] = new CacheEntry(bii,
                name);
        StatImageCache.CACHE_SIZE++;
    }

    private static void expandCache() {
        final var tempCache = new CacheEntry[StatImageCache.cache.length
                + StatImageCache.CACHE_INCREMENT];
        for (var x = 0; x < StatImageCache.CACHE_SIZE; x++) {
            tempCache[x] = StatImageCache.cache[x];
        }
        StatImageCache.cache = tempCache;
    }

    // Methods
    static BufferedImageIcon getCachedImage(final String name) {
        if (!StatImageCache.isInCache(name)) {
            final var bii = StatImageManager
                    .getUncachedImage(name);
            final var fixed = ImageTransformer
                    .getTransformedImage(bii,
                            ImageTransformer.getGraphicSize());
            StatImageCache.addToCache(name, fixed);
        }
        for (final CacheEntry element : StatImageCache.cache) {
            if (name.equals(element.getName())) {
                return element.getImage();
            }
        }
        return null;
    }

    static synchronized boolean isInCache(final String name) {
        if (StatImageCache.cache == null) {
            StatImageCache.cache = new CacheEntry[StatImageCache.CACHE_INCREMENT];
        }
        for (var x = 0; x < StatImageCache.CACHE_SIZE; x++) {
            if (name.equals(StatImageCache.cache[x].getName())) {
                return true;
            }
        }
        return false;
    }
}