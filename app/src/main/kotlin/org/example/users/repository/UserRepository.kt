package org.example.users.repository

import org.example.users.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String>{
    fun findByEmail(email: String): User?
    override fun existsById(id: String):Boolean
}