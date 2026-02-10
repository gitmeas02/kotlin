package org.example.security

import org.example.users.controller.authentication.CustomUserDetailsService
import org.example.users.controller.authentication.JwtAuthorizationFilter
import org.example.users.controller.authentication.JwtTokenUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: CustomUserDetailsService
){
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun jwtTokenUtil(): JwtTokenUtil {
        return JwtTokenUtil()
    }
    @Bean
    fun authManager(http: HttpSecurity): AuthenticationManager{
        val authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java
        )
        authenticationManagerBuilder.userDetailsService(userDetailsService)
        return authenticationManagerBuilder.build();
    }
    @Bean
    fun SecurityfilterChain(http: HttpSecurity, authenticationManager: AuthenticationManager, jwtTokenUtil: JwtTokenUtil): SecurityFilterChain{
        // Put your endpoint to create/sign, otherwise spring will secure it as
        // well you won't be able to do any request
        http
            .authorizeHttpRequests { authz ->
                authz.requestMatchers("/api/auth/**", "/health").permitAll()
                authz.anyRequest().authenticated()
            }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .authenticationManager(authenticationManager)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilter(JwtAuthorizationFilter(authenticationManager,jwtTokenUtil,userDetailsService))

        return http.build();
    }
    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder{
        return BCryptPasswordEncoder();
    }

}