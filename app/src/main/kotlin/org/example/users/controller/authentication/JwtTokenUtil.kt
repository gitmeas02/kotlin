package org.example.users.controller.authentication

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component // a core building block used to tell the Spring framework to automatically detect,
class JwtTokenUtil {
    private val secretString = "your-super-secret-key-that-must-be-very-long-at-least-64-characters-for-hs512"
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretString.toByteArray())

    fun generateToken(username:String): String {
        val now = Date()
        val expiration = Date(now.time + 90*60*1000)
        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .setExpiration(expiration)
            .signWith(secretKey)
            .compact()
    }
    fun getClaims(token: String): Claims {
       return Jwts.parser()
           .verifyWith(secretKey)
            .build()
           .parseSignedClaims(token)
           .payload
    }
    fun getEmail(token: String): String = getClaims(token).subject

    fun isTokenValid(token: String): Boolean{
        return try{
            println("Validating token: $token")
            val claims = getClaims(token)
            val expectationDate = claims.expiration
            val isValid = expectationDate.after(Date())
            println("Token valid: $isValid")
            isValid
        }catch(e: Exception){
            println("Token validation error: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}