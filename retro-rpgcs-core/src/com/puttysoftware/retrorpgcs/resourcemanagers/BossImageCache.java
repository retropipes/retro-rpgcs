/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.resourcemanagers;

import com.puttysoftware.images.BufferedImageIcon;

public class BossImageCache {
    // Fields
    private static CacheEntry[] cache;
    private static int CACHE_INCREMENT = 20;
    private static int CACHE_SIZE = 0;

    static synchronized void addToCache(final String name,
            final BufferedImageIcon bii) {
        if (BossImageCache.cache == null) {
            BossImageCache.cache = new CacheEntry[BossImageCache.CACHE_INCREMENT];
        }
        if (BossImageCache.CACHE_SIZE == BossImageCache.cache.length) {
            BossImageCache.expandCache();
        }
        BossImageCache.cache[BossImageCache.CACHE_SIZE] = new CacheEntry(bii,
                name);
        BossImageCache.CACHE_SIZE++;
    }

    private static void expandCache() {
        final var tempCache = new CacheEntry[BossImageCache.cache.length
                + BossImageCache.CACHE_INCREMENT];
        for (var x = 0; x < BossImageCache.CACHE_SIZE; x++) {
            tempCache[x] = BossImageCache.cache[x];
        }
        BossImageCache.cache = tempCache;
    }

    // Methods
    static BufferedImageIcon getCachedImage(final String name) {
        if (!BossImageCache.isInCache(name)) {
            final var bii = BossImageManager
                    .getUncachedImage(name);
            BossImageCache.addToCache(name, bii);
        }
        for (final CacheEntry element : BossImageCache.cache) {
            if (name.equals(element.getName())) {
                return element.getImage();
            }
        }
        return null;
    }

    static synchronized boolean isInCache(final String name) {
        if (BossImageCache.cache == null) {
            BossImageCache.cache = new CacheEntry[BossImageCache.CACHE_INCREMENT];
        }
        for (var x = 0; x < BossImageCache.CACHE_SIZE; x++) {
            if (name.equals(BossImageCache.cache[x].getName())) {
                return true;
            }
        }
        return false;
    }
}