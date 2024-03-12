package com.forlks.kssecurityexam.common.config

import com.forlks.kssecurityexam.common.cache.MeCacheType
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import java.util.concurrent.TimeUnit


@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun caffeineCaches(): List<CaffeineCache> {
        return Arrays.stream(MeCacheType.entries.toTypedArray())
                .map { cache ->
                    CaffeineCache(cache.cacheName, Caffeine.newBuilder().recordStats()
                            .expireAfterWrite(cache.expiredAfterWrite, cache.timeUnit)
                            .maximumSize(cache.maximumSize)
                            .build())
                }
                .toList()
    }

    @Bean
    fun cacheManager(caffeineCaches: List<CaffeineCache?>?): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(caffeineCaches!!)

        return cacheManager
    }
}
