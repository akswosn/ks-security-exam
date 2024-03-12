package com.forlks.kssecurityexam.common.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.forlks.kssecurityexam.common.component.JwtTokenProvider
import com.forlks.kssecurityexam.common.exception.KsException
import com.forlks.kssecurityexam.common.response.KsResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean


class JwtAuthorizeFilter(
        private val jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean() {
    private val log = KotlinLogging.logger {}
    private val authorizationPrefix = "Bearer "

    @Throws(KsException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, filterChain: FilterChain) {

        try{
            val token: String? = resolveToken(req as HttpServletRequest)
            log.info("Extracting token from HttpServletRequest: {}", token)

            if (token != null && jwtTokenProvider.validateToken(token)) {
                val auth: Authentication = jwtTokenProvider.getAuthentication(token)

                if (auth !is AnonymousAuthenticationToken) {
                    val context = SecurityContextHolder.createEmptyContext()
                    context.authentication = auth
                    SecurityContextHolder.setContext(context)
                }
            }
            filterChain.doFilter(req, res)
        }

        catch (e: KsException){
            log.error("#### KsException ::: $e")
            val json = ObjectMapper()
                    .writeValueAsString(e.ksResponse().toResponse())
            res.writer.write(json);
        }

        catch (e: Exception){
            log.error("#### Unchecked Exception ::: $e")
            val json = ObjectMapper()
                    .writeValueAsString(KsResponse.KS_SERVER_INTERNAL_ERROR.toResponse())
            res.writer.write(json);
        }

    }


    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION)

        log.info("#### bearerToken=$bearerToken, prefix=$authorizationPrefix, key=${HttpHeaders.AUTHORIZATION}")

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(authorizationPrefix)) {
            return bearerToken.substring(7)
        }
        return bearerToken
    }
}
