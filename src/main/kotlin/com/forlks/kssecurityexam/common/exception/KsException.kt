package com.forlks.kssecurityexam.common.exception

import com.forlks.kssecurityexam.common.response.KsResponse
import java.lang.Exception

abstract class KsException(
        ksResponse: KsResponse,
        e: Exception
) : RuntimeException(ksResponse.resultMsg(), e) {

    abstract fun ksResponse(): KsResponse

    abstract fun e(): Exception
}
