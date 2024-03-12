package com.forlks.kssecurityexam.common.exception

import org.springframework.web.reactive.function.client.WebClientException

class ClientApiBadRequestError(
        val errorCode: String = "Client Bad Request",
        val timestamp: String = "",
        val status: String = "",
        val error: String = "",
        val path: String = "",
        override val message: String = "Client Bad Request"
) : WebClientException("Client Bad Request") {}
