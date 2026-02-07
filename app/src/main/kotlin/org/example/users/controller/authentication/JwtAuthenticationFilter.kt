package org.example.users.controller.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.coyote.Response
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.Collections
import java.util.Date

class JwtAuthenticationFilter (
    private val jwtTokenUtil: JwtTokenUtil,
    authenticationManager: AuthenticationManager
    ) : UsernamePasswordAuthenticationFilter(){
    init {
        this.authenticationManager = authenticationManager
        setFilterProcessesUrl("/login")
    }
    override fun attemptAuthentication(req: HttpServletRequest, res: HttpServletResponse): Authentication{
        val credential = ObjectMapper().readValue(req.inputStream, LoginRequest::class.java)
        val auth = UsernamePasswordAuthenticationToken(
            credential.email,
            credential.password,
            Collections.singleton(SimpleGrantedAuthority("user"))
        )
        return authenticationManager.authenticate(auth);
    }
    override fun successfulAuthentication(
        req: HttpServletRequest?, res: HttpServletResponse,chain: FilterChain?,
        auth: Authentication
    ){
        val username = (auth.principal as UserSecurity).username
        val token: String = jwtTokenUtil.generateToken(username)
        res.addHeader("Authorization",token)
        res.addHeader("Access-Control-Expose-Headers", "Authorization")
    }
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ){
        val error = BadCredentialsError(message = "Email or password incorrect")
        response.status = error.status
        response.contentType ="application/json"
        response.writer.append(error.toString())
    }
    private data class  BadCredentialsError(
        val timestamp:Long = Date().time,
        val status:Int = 401,
        val message: String ="Email or password incorrect"
        ){
        override fun toString():String{
            return ObjectMapper().writeValueAsString(this)
        }
    }

}