package com.rv.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CacheInspectionService {
    private final CacheManager cacheManager;

    public CacheInspectionService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void printCacheContents(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            System.out.println("Cache contents for cache: " + cacheName);
            System.out.println(Objects.requireNonNull(cache.getNativeCache()).toString());
        } else {
            System.out.println("Cache not found: " + cacheName);
        }
    }
}
