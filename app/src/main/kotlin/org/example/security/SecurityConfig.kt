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

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: CustomUserDetailsService
){
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
            .cors { } // Enable CORS with default configuration
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