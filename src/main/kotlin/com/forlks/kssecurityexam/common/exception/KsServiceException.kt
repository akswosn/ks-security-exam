package com.forlks.kssecurityexam.common.exception

import com.forlks.kssecurityexam.common.response.KsResponse
import java.lang.Exception

// API Exception
data class KsServiceException(
        val ksResponse: KsResponse,
        val e: Exception,
) : KsException(ksResponse, e) {
    val code: Int = ksResponse.code()
    val statusCode: String = ksResponse.statusCode()
    val errMsg: String = ksResponse.resultMsg()


    override fun ksResponse(): KsResponse = ksResponse

    override fun e(): Exception = e
}
