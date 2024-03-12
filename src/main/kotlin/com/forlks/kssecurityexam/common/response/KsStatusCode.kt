package com.forlks.kssecurityexam.common.response

import java.io.Serializable

interface KsStatusCode: Serializable {

    fun code(): Int
    fun statusCode(): String?;
    fun resultMsg(): String?;
}
