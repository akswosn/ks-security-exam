package com.forlks.kssecurityexam.common.component

import ch.qos.logback.classic.PatternLayout.HEADER_PREFIX
import com.forlks.kssecurityexam.common.dto.JwtPayloadDto
import com.forlks.kssecurityexam.common.exception.KsAPIException
import com.forlks.kssecurityexam.common.response.KsResponse
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtTokenProvider(
        @Value("\${jwt.secret.key}")
        private val secretKey: String,
        @Value("\${jwt.expiration-min}")
        private val expiration: Long,
        @Value("\${jwt.issuer}")
        private val issuer: String,
) {
    lateinit var key: SecretKey
    private val log = KotlinLogging.logger {}

    private val headerJwtPrefix = "Bearer "

    @PostConstruct
    fun init(){
        val secret = Base64.getEncoder().encodeToString(secretKey.toByteArray())
        key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

//
    @Throws(exceptionClasses = [KsAPIException::class])
    fun createToken(dto: JwtPayloadDto): String = try {
        Jwts.builder()
            .signWith(key, Jwts.SIG.HS256)
            .issuer(issuer)
            .subject(dto.userId)
            .claims(dto.getClaim())
            .issuedAt(Timestamp.valueOf(LocalDateTime.now()))
            .expiration(Date.from(Instant.now().plus(expiration, ChronoUnit.MINUTES)))
            .compact()
    } catch (e: Exception){
        throw KsAPIException(KsResponse.KS_NOT_AUTHORIZE, e)
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload

        val authorities: MutableList<GrantedAuthority> = ArrayList()
            authorities.add((SimpleGrantedAuthority(claims["roles"].toString())))

        val principal = User(claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    @Throws(exceptionClasses = [KsAPIException::class])
    fun validateToken(token: String?): Boolean {
        try {
            val claims: Jws<Claims> = Jwts
                    .parser().verifyWith(key).build()
                    .parseSignedClaims(token)
            //  parseClaimsJws will check expiration date. No need do here.
            log.info("expiration date: {}", claims.payload.expiration)
            return true
        } catch (e: JwtException) {
            log.error("Invalid JWT token: {}", e.message)
            throw KsAPIException(KsResponse.KS_TOKEN_INVALID, e)

        } catch (e: IllegalArgumentException) {
            log.error("Invalid JWT token: {}", e.message)
            throw KsAPIException(KsResponse.KS_TOKEN_INVALID, e)
        }
    }

    /**
     * 토큰 가져오기
     */
    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(headerJwtPrefix)) {
            return bearerToken.replaceFirst(headerJwtPrefix, "")
        }
        return null
    }

    fun getUserId(token: String?): String = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).payload.subject


    fun getUsersId(token: String?) = try {
        log.info("#### ${Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).payload}")
        val result = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).payload.get("usersId", Integer::class.java)
        result.toLong()
    } catch (e: Exception){
        log.error ("### JWT parse error ::: $e")
        0L
    }
}
