package com.forlks.kssecurityexam.common.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

enum class KsResponse(
        private val resultCode: Int,
        private val statusCode: String,
        private val resultMsg: String
): KsStatusCode {
    //Code 정의
    KS_SUCCESS(200, "KS1000", "OK"),
    //클라이언트 요청 에러
    KS_NOT_AUTHORIZE(401, "KS0400", "Not Authorize User"),
    KS_NOT_USER(401, "KS0401", "Not found User"),
    KS_TOKEN_INVALID(401, "KS0402", "Invalid Access Token"),
    KS_CONFLICT(409, "KS0403", "Request could not be completed "),
    //서버에서 정의된 오류
    KS_NEGATIVE_NUMBER(409, "KS0404", "NEGATIVE_NUMBER"),
    KS_EXIST_MEMBER(409, "KS0405", "KS_EXIST_USER"),
    KS_INVALID_MEMBER_PASSWORD(409, "KS0406", "KS_INVALID_USER_PASSWORD"),

    //서버 에러
    KS_SERVER_INTERNAL_ERROR(500, "KS5001", "Interval Server Error"),
    KS_INVALID_PARAM(500, "KS5002", "Invalid Parameter"),
    KS_BAD_REQUEST(500, "KS5003", "Bad Request");


    fun toResponse(): ResponseEntity<KsResponseEntity> {
        return ResponseEntity(
                KsResponseEntity(
                        code = this.resultCode,
                        resultMsg = resultMsg,
                        statusCode = statusCode,
                        dataCount = 0
                ), HttpStatus.valueOf(this.resultCode)
        )
    }


    override fun code(): Int {
        return this.resultCode
    }

    override fun statusCode(): String {
        return statusCode
    }

    override fun resultMsg(): String {
        return resultMsg
    }

    //성공 데이터 바인딩용
    fun <T> toDataResponse(data: T): ResponseEntity<KsResponseEntity> {
        return ResponseEntity(
                KsResponseEntity(
                        code = this.resultCode,
                        resultMsg = this.resultMsg,
                        statusCode = this.statusCode,
                        data = data,
                        dataCount = 1
                ), HttpStatus.valueOf(this.resultCode)
        )
    }

    fun <T> toDataResponse(data: T, count: Int?): ResponseEntity<KsResponseEntity> {
        return ResponseEntity(
                KsResponseEntity(
                        code = this.resultCode,
                        resultMsg = this.resultMsg,
                        statusCode = this.statusCode,
                        data = data,
                        dataCount = count
                ), HttpStatus.valueOf(this.resultCode)
        )
    }

    //error msg 세팅용
    fun toMsgResponse(errMsg: String?): ResponseEntity<KsResponseEntity> {

        return ResponseEntity(
                KsResponseEntity(
                        code = this.resultCode,
                        resultMsg = errMsg,
                        statusCode = this.statusCode,
                        dataCount = 0
                ), HttpStatus.valueOf(this.resultCode)
        )
    }
}
