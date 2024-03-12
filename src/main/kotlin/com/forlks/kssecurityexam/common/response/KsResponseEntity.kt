package com.forlks.kssecurityexam.common.response

import java.io.Serializable

class KsResponseEntity (
        val code: Int = 0, //int 형 코드
        val statusCode: String? = null, //상태코드
        val resultMsg: String? = null,
        val dataCount: Int? = null,
        val data: Any? = null,
) : Serializable {


}
