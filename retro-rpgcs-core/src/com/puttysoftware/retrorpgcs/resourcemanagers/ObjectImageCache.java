/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.resourcemanagers;

import com.puttysoftware.images.BufferedImageIcon;

public class ObjectImageCache {
    // Fields
    private static CacheEntry[] cache;
    private static int CACHE_INCREMENT = 20;
    private static int CACHE_SIZE = 0;

    static synchronized void addToCache(final String name,
            final BufferedImageIcon bii) {
        if (ObjectImageCache.cache == null) {
            ObjectImageCache.cache = new CacheEntry[ObjectImageCache.CACHE_INCREMENT];
        }
        if (ObjectImageCache.CACHE_SIZE == ObjectImageCache.cache.length) {
            ObjectImageCache.expandCache();
        }
        ObjectImageCache.cache[ObjectImageCache.CACHE_SIZE] = new CacheEntry(
                bii, name);
        ObjectImageCache.CACHE_SIZE++;
    }

    private static void expandCache() {
        final var tempCache = new CacheEntry[ObjectImageCache.cache.length
                + ObjectImageCache.CACHE_INCREMENT];
        for (var x = 0; x < ObjectImageCache.CACHE_SIZE; x++) {
            tempCache[x] = ObjectImageCache.cache[x];
        }
        ObjectImageCache.cache = tempCache;
    }

    // Methods
    static BufferedImageIcon getCachedImage(final String name,
            final String baseName) {
        if (!ObjectImageCache.isInCache(name)) {
            final var bii = ObjectImageManager
                    .getUncachedImage(baseName);
            ObjectImageCache.addToCache(name, bii);
        }
        for (final CacheEntry element : ObjectImageCache.cache) {
            if (name.equals(element.getName())) {
                return element.getImage();
            }
        }
        return null;
    }

    static synchronized boolean isInCache(final String name) {
        if (ObjectImageCache.cache == null) {
            ObjectImageCache.cache = new CacheEntry[ObjectImageCache.CACHE_INCREMENT];
        }
        for (var x = 0; x < ObjectImageCache.CACHE_SIZE; x++) {
            if (name.equals(ObjectImageCache.cache[x].getName())) {
                return true;
            }
        }
        return false;
    }
}