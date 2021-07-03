/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.resourcemanagers;

import com.puttysoftware.images.BufferedImageIcon;

public class BattleImageCache {
    // Fields
    private static CacheEntry[] cache;
    private static int CACHE_INCREMENT = 20;
    private static int CACHE_SIZE = 0;

    static synchronized void addToCache(final String name,
            final BufferedImageIcon bii) {
        if (BattleImageCache.cache == null) {
            BattleImageCache.cache = new CacheEntry[BattleImageCache.CACHE_INCREMENT];
        }
        if (BattleImageCache.CACHE_SIZE == BattleImageCache.cache.length) {
            BattleImageCache.expandCache();
        }
        BattleImageCache.cache[BattleImageCache.CACHE_SIZE] = new CacheEntry(
                bii, name);
        BattleImageCache.CACHE_SIZE++;
    }

    private static void expandCache() {
        final var tempCache = new CacheEntry[BattleImageCache.cache.length
                + BattleImageCache.CACHE_INCREMENT];
        for (var x = 0; x < BattleImageCache.CACHE_SIZE; x++) {
            tempCache[x] = BattleImageCache.cache[x];
        }
        BattleImageCache.cache = tempCache;
    }

    // Methods
    static BufferedImageIcon getCachedImage(final String name,
            final String baseName) {
        if (!BattleImageCache.isInCache(name)) {
            final var bii = BattleImageManager
                    .getUncachedImage(baseName);
            BattleImageCache.addToCache(name, bii);
        }
        for (final CacheEntry element : BattleImageCache.cache) {
            if (name.equals(element.getName())) {
                return element.getImage();
            }
        }
        return null;
    }

    static synchronized boolean isInCache(final String name) {
        if (BattleImageCache.cache == null) {
            BattleImageCache.cache = new CacheEntry[BattleImageCache.CACHE_INCREMENT];
        }
        for (var x = 0; x < BattleImageCache.CACHE_SIZE; x++) {
            if (name.equals(BattleImageCache.cache[x].getName())) {
                return true;
            }
        }
        return false;
    }
}