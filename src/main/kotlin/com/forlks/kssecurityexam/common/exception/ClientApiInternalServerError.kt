package com.forlks.kssecurityexam.common.exception

import org.springframework.web.reactive.function.client.WebClientException

class ClientApiInternalServerError (
        val errorCode: String = "OClient APi Internal Server Error",
        val timestamp: String = "",
        val status: String = "",
        val error: String = "",
        val path: String = "",
        override val message: String = "Client APi Internal Server Error"
) : WebClientException("Client APi Internal Server Error") {}
