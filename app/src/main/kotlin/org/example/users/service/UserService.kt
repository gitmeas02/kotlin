package org.example.users.service

import org.example.users.entity.Role
import org.example.users.entity.User
import org.example.users.repository.RoleRepository
import org.example.users.repository.UserRepository
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserService(
    private val repository: UserRepository,
    private val roleRepository: RoleRepository
){
    // REST API services
    // create / read / update / delete 
    fun createUser(name: String,email:String): User{
        // logic to save user to database
        val now = Instant.now()
        val role = getOrCreateRole("user")
        val user = User(
            name = name,
            email = email,
            createdAt = now,
            updatedAt = now,
            roles = mutableSetOf(role)
        )
        val savedUser = repository.save(user)
        return savedUser
    }
    fun getAllUsers(page:Int, size: Int): Page<User> {
        var pageable: Pageable = PageRequest.of(page,size)
        return repository.findAll(pageable)
    }
    fun deleteUser(id: String): Boolean{
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else {
            false
        }
    }
    fun emailExists(email: String): Boolean {
        return repository.findByEmail(email) != null
    }
    fun IdExists(id: String): Boolean{
        return repository.existsById(id)
    }
    // find user by id

    fun findUserById(id: String): User? {
        val user = this.repository.findById(id)
        println(user.isPresent) //
        if(user.isPresent == false) return null
        else return user.get()
    }

    private fun getOrCreateRole(name: String): Role {
        return roleRepository.findById(name)
            .orElseGet { roleRepository.save(Role(name = name)) }
    }
}