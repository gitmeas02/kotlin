package org.example.users.controller.authentication

import com.sun.net.httpserver.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.SecurityContextHolderFilter

class JwtAuthorizationFilter(
    private val authManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil,
    private val service: CustomUserDetailsService
): BasicAuthenticationFilter(authManager) {
    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain
    ){
        val requestUri = req.requestURI
        println("=== JWT FILTER === URI: $requestUri")
        
        // Skip JWT validation for public endpoints
        if (requestUri.startsWith("/api/auth/") || requestUri == "/health") {
            println("Public endpoint, skipping JWT validation")
            chain.doFilter(req, res)
            return
        }
        
        val header = req.getHeader(HttpHeaders.AUTHORIZATION)
        println("Authorization header: $header")
        
        if(header == null || !header.startsWith("Bearer ")){
            println("No valid Bearer token found, proceeding without authentication")
            chain.doFilter(req,res)
            return
        }
        val token = header.substring(7)
        println("Extracted token: $token")
        getAuthentication(token)?.also{
            println("Authentication set for user: ${it.principal}")
            SecurityContextHolder.getContext().authentication= it
        }
        chain.doFilter(req,res)

    }
    private fun getAuthentication(token:String): UsernamePasswordAuthenticationToken?{
        if(!jwtTokenUtil.isTokenValid(token)) return null
        val email = jwtTokenUtil.getEmail(token)
        val user = service.loadUserByUsername(email)
        return UsernamePasswordAuthenticationToken(user,null,user.authorities)
    }

}