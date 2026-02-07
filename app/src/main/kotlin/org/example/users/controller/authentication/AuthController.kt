package org.example.users.controller.authentication

import org.example.users.ApiResponse
import org.example.users.entity.User
import org.example.users.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil
) {

    @PostMapping("/register")
    fun register(@Validated @RequestBody requestSignUp: SignUp): ResponseEntity<ApiResponse<UserRegistrationData?>> {
        // Validate required fields
        if (requestSignUp.email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    success = false,
                    message = "Email is required",
                    data = null
                )
            )
        }
        
        if (requestSignUp.password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    success = false,
                    message = "Password is required",
                    data = null
                )
            )
        }
        
        // Validate name field
        if (requestSignUp.name.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    success = false,
                    message = "Missing name field",
                    data = null
                )
            )
        }
        
        // Validate email format
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!requestSignUp.email.matches(emailRegex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    success = false,
                    message = "Incorrect email format",
                    data = null
                )
            )
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(requestSignUp.email.lowercase().trim()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse(
                    success = false,
                    message = "Email already exists. Please use a different email address.",
                    data = null
                )
            )
        }
        
        // Validate password length
        if (requestSignUp.password.length < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    success = false,
                    message = "Password must be at least 6 characters long",
                    data = null
                )
            )
        }
        
        try {
            val hashedPassword = passwordEncoder.encode(requestSignUp.password)
            val jwtUser = User(
                name = requestSignUp.name?.ifBlank { "User" } ?: "User", 
                email = requestSignUp.email.lowercase().trim(), 
                password = hashedPassword
            )
            val savedUser = userRepository.save(jwtUser)
            
            val responseData = UserRegistrationData(
                id = savedUser.id,
                email = savedUser.email,
                name = savedUser.name
            )
            
            return ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "User registered successfully",
                    data = responseData
                )
            )
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse(
                    success = false,
                    message = "Registration failed: ${e.message}",
                    data = null
                )
            )
        }
    }

    @PostMapping("/login")
    fun login(@Validated @RequestBody loginRequest: LoginRequest): ResponseEntity<ApiResponse<LoginData?>> {
        // Validate required fields
        if (loginRequest.email.isBlank() || loginRequest.password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    success = false,
                    message = "Invalid email and password",
                    data = null
                )
            )
        }
        
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.email.lowercase().trim(),
                    loginRequest.password
                )
            )
            SecurityContextHolder.getContext().authentication = authentication
            
            val token = jwtTokenUtil.generateToken(loginRequest.email.lowercase().trim())
            println("Generated token: $token")
            
            val responseData = LoginData(
                token = token,
                email = loginRequest.email.lowercase().trim()
            )
            
            return ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "Login successful",
                    data = responseData
                )
            )
        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse(
                    success = false,
                    message = "Invalid email and password",
                    data = null
                )
            )
        }
    }
}

data class SignUp(
    val email: String,
    val password: String,
    val name: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserRegistrationData(
    val id: Long,
    val email: String,
    val name: String
)

data class LoginData(
    val token: String,
    val email: String
)