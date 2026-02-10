package org.example.users.repository

import org.example.users.entity.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, String> {
    // No need for findByName since name IS the @Id
    // Use findById(name) or getById(name) instead
}
