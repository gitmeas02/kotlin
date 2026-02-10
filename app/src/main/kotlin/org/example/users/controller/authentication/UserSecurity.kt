package org.example.users.controller.authentication

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserSecurity (
    val id: String,
    val email: String,
    private val userPassword: String,
    private val uAuthorities: MutableCollection<GrantedAuthority>
): UserDetails{
    override fun getAuthorities()= uAuthorities
    override fun getUsername() = email
    override  fun getPassword()= userPassword
    override  fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}