package com.forlks.kssecurityexam.common.client.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.forlks.kssecurityexam.common.exception.ClientApiBadRequestError
import com.forlks.kssecurityexam.common.exception.ClientApiInternalServerError
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient


/**
 * TEST
 */
//@Configuration
class TestApiConfig (
        @Value("\${api.server.quotation-api.url}")
        private val url: String,
){
    private val log = KotlinLogging.logger {}

    @Bean
    fun quotationApiWebClient(): WebClient {
        val connector = getConnector()
        val filter = getCommonFilter()
        return getWebClient(url, filter, connector)
//        return getWebClient("", filter, connector)
    }

    private fun getConnector() =
            ReactorClientHttpConnector(HttpClient.create().wiretap(true))// Wire Logger 사용 (트랙픽 로깅을 위한 wire 로깅 활성화)

    private fun getCommonFilter(): ExchangeFilterFunction =
            ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
                if (clientResponse.statusCode().is4xxClientError) {
                    clientResponse.bodyToMono(ClientApiBadRequestError::class.java)
                            .flatMap { errorDetail ->
                                Mono.error(
                                        ClientApiBadRequestError(
                                                errorCode = errorDetail.errorCode,
                                                timestamp = errorDetail.timestamp,
                                                status = errorDetail.status,
                                                error = errorDetail.error,
                                                message = errorDetail.message
                                        )
                                )
                            }
                } else if (clientResponse.statusCode().is5xxServerError) {
                    clientResponse.bodyToMono(ClientApiInternalServerError::class.java)
                            .flatMap { errorDetail ->
                                Mono.error(
                                        ClientApiInternalServerError(
                                                errorCode = errorDetail.errorCode,
                                                timestamp = errorDetail.timestamp,
                                                status = errorDetail.status,
                                                error = errorDetail.error,
                                                message = errorDetail.message
                                        )
                                )
                            }
                } else {
                    Mono.just(clientResponse)
                }
            }

    private fun getWebClient(
            baseUrl: String,
            filter: ExchangeFilterFunction,
            connector: ReactorClientHttpConnector
    ): WebClient =
            WebClient.builder().baseUrl(baseUrl).defaultHeaders { headers ->
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                headers.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
            }.exchangeStrategies(
                    ExchangeStrategies.builder()
                            .codecs { configurer: ClientCodecConfigurer -> acceptedCodecs(configurer) }.build()
            ).filter(filter).clientConnector(connector).build()
}

private fun acceptedCodecs(clientCodecConfigurer: ClientCodecConfigurer) {
    clientCodecConfigurer.defaultCodecs().maxInMemorySize(25 * 1024 * 1024)
    clientCodecConfigurer.customCodecs()
            .registerWithDefaultConfig(Jackson2JsonDecoder(ObjectMapper(), MediaType.APPLICATION_JSON))
    clientCodecConfigurer.customCodecs()
            .registerWithDefaultConfig(Jackson2JsonEncoder(ObjectMapper(), MediaType.APPLICATION_JSON))
}
