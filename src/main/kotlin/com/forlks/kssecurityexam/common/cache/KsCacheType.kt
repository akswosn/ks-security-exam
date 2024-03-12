package com.forlks.kssecurityexam.common.cache

import java.util.concurrent.TimeUnit

enum class KsCacheType(
        val cacheName: String = "",
        val expiredAfterWrite: Long = 0,
        val maximumSize: Long = 0,
        val timeUnit: TimeUnit = TimeUnit.MILLISECONDS
) {


}
