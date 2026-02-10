package org.example.users.controller.authentication

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.example.users.ApiResponse
import org.example.users.entity.User
import org.example.users.entity.Role
import org.example.users.repository.RoleRepository
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
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody requestSignUp: SignUp): ResponseEntity<ApiResponse<UserRegistrationData?>> {
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

        try {
            val hashedPassword = passwordEncoder.encode(requestSignUp.password)
            val defaultRole = roleRepository.findById("user")
                .orElseGet { roleRepository.save(Role(name = "user")) }
            val jwtUser = User(
                name = requestSignUp.name,
                email = requestSignUp.email.lowercase().trim(),
                password = hashedPassword,
                roles = mutableSetOf(defaultRole)
            )
            val savedUser = userRepository.save(jwtUser)

            val responseData = UserRegistrationData(
                id = savedUser.uuid,
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
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<ApiResponse<LoginData?>> {
        try {
            val normalizedEmail = loginRequest.email.lowercase().trim()
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    normalizedEmail,
                    loginRequest.password
                )
            )
            SecurityContextHolder.getContext().authentication = authentication

            val role = authentication.authorities.firstOrNull()?.authority ?: "user"

            val user = userRepository.findByEmail(normalizedEmail)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse(
                        success = false,
                        message = "Invalid email and password",
                        data = null
                    )
                )

            val accessToken = jwtTokenUtil.generateAccessToken(user, role)
            val refreshToken = jwtTokenUtil.generateRefreshToken(user)

            val responseData = LoginData(
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = LoginUserData(
                    id = user.uuid,
                    username = user.name,
                    email = user.email,
                    avatar = user.avatar
                )
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

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody refreshRequest: RefreshTokenRequest): ResponseEntity<ApiResponse<RefreshTokenData?>> {
        try {
            val refreshToken = refreshRequest.refreshToken

            // Validate refresh token
            if (!jwtTokenUtil.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse(
                        success = false,
                        message = "Invalid refresh token",
                        data = null
                    )
                )
            }

            // Get user email from token
            val email = jwtTokenUtil.getEmail(refreshToken)
            val user = userRepository.findByEmail(email)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse(
                        success = false,
                        message = "User not found",
                        data = null
                    )
                )

            // Verify token version
            val tokenVersion = jwtTokenUtil.getTokenVersion(refreshToken)
            if (tokenVersion != user.tokenVersion) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse(
                        success = false,
                        message = "Token has been revoked",
                        data = null
                    )
                )
            }

            // Generate new access token
            val role = user.roles.firstOrNull()?.name ?: "user"
            val newAccessToken = jwtTokenUtil.generateAccessToken(user, role)

            val responseData = RefreshTokenData(
                accessToken = newAccessToken
            )

            return ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "Token refreshed successfully",
                    data = responseData
                )
            )
        } catch (e: Exception) {
            println("Refresh token error: ${e.message}")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse(
                    success = false,
                    message = "Invalid or expired refresh token",
                    data = null
                )
            )
        }
    }

    data class SignUp(
        @field:NotBlank(message = "Email is required")
        @field:Email(message = "Invalid email format")
        val email: String,

        @field:NotBlank(message = "Password is required")
        @field:Size(min = 6, message = "Password must be at least 6 characters long")
        val password: String,

        @field:NotBlank(message = "Name is required")
        val name: String
    )

    data class LoginRequest(
        @field:NotBlank(message = "Email is required")
        @field:Email(message = "Invalid email format")
        val email: String,

        @field:NotBlank(message = "Password is required")
        val password: String
    )

    data class UserRegistrationData(
        val id: String,
        val email: String,
        val name: String
    )

    data class LoginData(
        val accessToken: String,
        val refreshToken: String,
        val user: LoginUserData
    )

    data class LoginUserData(
        val id: String,
        val username: String,
        val email: String,
        val avatar: String?
    )

    data class RefreshTokenRequest(
        @field:NotBlank(message = "Refresh token is required")
        val refreshToken: String
    )

    data class RefreshTokenData(
        val accessToken: String
    )
}