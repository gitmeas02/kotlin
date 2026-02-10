package org.example.users.controller.authentication

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.example.users.entity.User
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component // a core building block used to tell the Spring framework to automatically detect,
class JwtTokenUtil {
    private val secretString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#"
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretString.toByteArray())

    private val accessTokenExpiryMs = 15 * 60 * 1000L
    private val refreshTokenExpiryMs = 30L * 24 * 60 * 60 * 1000

    fun generateAccessToken(user: User, role: String): String {
        val now = Date()
        val expiration = Date(now.time + accessTokenExpiryMs)
        return Jwts.builder()
            .subject(user.uuid)
            .issuedAt(now)
            .expiration(expiration)
            .claim("role", role)
            .claim("type", "access")
            .claim("email", user.email)
            .signWith(secretKey)
            .compact()
    }

    fun generateAccessTokenForUserSecurity(userId: String, email: String, role: String): String {
        val now = Date()
        val expiration = Date(now.time + accessTokenExpiryMs)
        return Jwts.builder()
            .subject(userId)
            .issuedAt(now)
            .expiration(expiration)
            .claim("role", role)
            .claim("type", "access")
            .claim("email", email)
            .signWith(secretKey)
            .compact()
    }

    fun generateRefreshToken(user: User): String {
        val now = Date()
        val expiration = Date(now.time + refreshTokenExpiryMs)
        return Jwts.builder()
            .subject(user.uuid)
            .issuedAt(now)
            .expiration(expiration)
            .claim("token_version", user.tokenVersion)
            .claim("type", "refresh")
            .claim("email", user.email)
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
    fun getEmail(token: String): String {
        val claims = getClaims(token)
        val email = claims["email"] as? String
        return email ?: claims.subject
    }

    fun isAccessToken(token: String): Boolean {
        val claims = getClaims(token)
        return claims["type"] == "access"
    }

    fun isRefreshToken(token: String): Boolean {
        val claims = getClaims(token)
        return claims["type"] == "refresh"
    }

    fun getTokenVersion(token: String): Int {
        val claims = getClaims(token)
        return claims["token_version"] as? Int ?: 0
    }

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