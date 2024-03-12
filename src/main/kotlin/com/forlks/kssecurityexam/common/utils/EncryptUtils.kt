package com.forlks.kssecurityexam.common.utils

import org.jasypt.encryption.StringEncryptor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class EncryptUtils (
        private val passwordEncoder: BCryptPasswordEncoder,
        private val jasyptStringEncryptor: StringEncryptor
){

    fun encryptTwoway(idValue: String): String = if(idValue.isEmpty()){
        ""
    }
    else {
        jasyptStringEncryptor.encrypt(idValue)
    }

    fun decrytTwoway(endIdValue: String) = if(endIdValue.isEmpty()){
        ""
    }
    else {
        jasyptStringEncryptor.decrypt(endIdValue)
    }


    fun encryptPassword(password: String): String = if(password.isEmpty()){
        ""
    }
    else {
        passwordEncoder.encode(password)
    }

    /**
     * 비밀번호체크
     */
    fun isPasswordCheck(inputPassword:String, password: String) = if(inputPassword.isEmpty() || password.isEmpty()){
        false
    }
    else {
        passwordEncoder.matches(inputPassword, password)
    }

}
