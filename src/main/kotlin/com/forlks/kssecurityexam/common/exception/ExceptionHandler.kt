package com.forlks.kssecurityexam.common.exception

import com.forlks.kssecurityexam.common.response.KsResponse
import com.forlks.kssecurityexam.common.response.KsResponseEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(KsException::class)
    fun badRequestError(ex: KsException): ResponseEntity<KsResponseEntity>? {
        return ex.ksResponse().toResponse()
    }

    @ExceptionHandler(ClientApiInternalServerError::class)
    fun internalServerError(ex: ClientApiInternalServerError): ResponseEntity<KsResponseEntity>? {
        return KsResponse.KS_SERVER_INTERNAL_ERROR.toMsgResponse(" Webclient common Internal Server Error :: " + ex
                .message)
    }

    @ExceptionHandler(ClientApiBadRequestError::class)
    fun webclientCommonBadRequestErrorHandler(ex: ClientApiBadRequestError): ResponseEntity<KsResponseEntity>? {
        return KsResponse.KS_BAD_REQUEST.toMsgResponse("Webclient Common Bad Request Error :: " + ex.message)
    }

}
