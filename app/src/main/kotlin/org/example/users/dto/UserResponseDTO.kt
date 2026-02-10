package org.example.users.dto

import org.example.users.entity.User
import java.time.Instant

data class UserResponseDTO(
    val uuid: String,
    val name: String,
    val email: String,
    val avatar: String?,
    val roles: List<RoleDTO>,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class RoleDTO(
    val name: String,
    val description: String?
)

fun User.toResponseDTO(): UserResponseDTO {
    return UserResponseDTO(
        uuid = this.uuid,
        name = this.name,
        email = this.email,
        avatar = this.avatar,
        roles = this.roles.map { RoleDTO(it.name, it.description) },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
