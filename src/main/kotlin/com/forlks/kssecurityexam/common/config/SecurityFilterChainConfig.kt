package com.forlks.kssecurityexam.common.config

import com.forlks.kssecurityexam.common.component.JwtTokenProvider
import com.forlks.kssecurityexam.common.filter.JwtAuthorizeFilter
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import java.util.*


@Configuration
class SecurityFilterChainConfig {
    private val allowedUrls = arrayOf(
            "/", "/swagger-ui/**", "/v3/api-docs/**", "/v1/user/signup", "/v1/user/login",
    )


    //CORS 설정
    fun corsConfigurationSource(): CorsConfigurationSource {
        return CorsConfigurationSource {
            val config = CorsConfiguration()
            config.allowedHeaders = Collections.singletonList("*")
            config.allowedMethods = Collections.singletonList("*")
//            config.setAllowedOriginPatterns(Collections.singletonList("http://localhost")) // client 존재시
            config.allowCredentials = true
            config
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity, tokenProvider: JwtTokenProvider) = http
            .authorizeHttpRequests {
                it.requestMatchers(*allowedUrls).permitAll()
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .anyRequest().authenticated()
            }
            // h2 web console iframe 오류 해결(X-Frame-Options' to 'deny')
            .headers { a -> a.frameOptions { o -> o.disable() } }
            .csrf { csrf -> csrf.disable() }
            .cors { corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(JwtAuthorizeFilter(tokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            .build()!!

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()


    @Bean
    fun webSecurityCustomizer() = WebSecurityCustomizer { web: WebSecurity ->
        web.ignoring().requestMatchers(*allowedUrls)
    }


    //TODO : UserDetails (DB 연동 하는 부분은 생략하자~~~)
}
