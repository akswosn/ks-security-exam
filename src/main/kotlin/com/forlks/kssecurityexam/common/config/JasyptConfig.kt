package com.forlks.kssecurityexam.common.config

import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class JasyptConfig (
        @Value("\${jasypt.encryptor.password}")
        val password: String
){

    @Bean("jasyptStringEncryptor")
    fun jasyptStringEncryptor(): StringEncryptor {
        val encryptor = PooledPBEStringEncryptor()
        val config = SimpleStringPBEConfig()
        config.setPassword(password)
        config.setPoolSize("1")
        config.setAlgorithm("PBEWithMD5AndDES")
        config.setStringOutputType("base64")
        config.setKeyObtentionIterations("1000")
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator")
        encryptor.setConfig(config)
        return encryptor
    }
}
