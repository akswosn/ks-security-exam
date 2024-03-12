package com.forlks.kssecurityexam.common.dto

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.security.core.Authentication

class JwtPayloadDto(
        //사용자 고유 번호
        val memberId: Long? = null,
        //사용자 ID
        val userId: String,
        //Role (개인 / 기업I
        val roles: String,
        //인증정보
) {

    fun getClaim(): Claims = Jwts.claims().subject(userId)
            .add("roles", roles)
            .add("id", memberId)
            .build()
    }

