package org.example.roles

import org.example.users.entity.Role
import org.example.users.entity.User
import org.example.users.repository.RoleRepository
import org.example.users.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleService(
    private val roleRepository: RoleRepository,
    private val userRepository : UserRepository
) {

     // role
    @Transactional
    fun createRole(role_name: String, descript_role: String? = null): Role{
        val newRole = Role().apply {
            name = role_name
            description = descript_role
        }
      val saveRole = roleRepository.save(newRole)
        return saveRole;
    }
    @Transactional
    fun deleleRoleById(role_id: String){
        val role = roleRepository.findById(role_id).orElseThrow{ Exception("Role Not found")}
        roleRepository.delete(role)
    }
    @Transactional
    fun updateRoleId(role_id: String): Role {
        val role = roleRepository.findById(role_id).orElseThrow{ Exception("Role Not found")}
        val saveupdateRoleById = roleRepository.save(role)
        return saveupdateRoleById
    }
    @Transactional
    fun getAllRole(): List<Role>{
        var role = roleRepository.findAll()
        return role;
    }
    // role and user
    @Transactional
    fun assignNewRole(role_name: String, user_id: String): User{
        val findUser = userRepository.findById(user_id)
            .orElseThrow { Exception("User with id $user_id not found") }
        val findRole = roleRepository.findById(role_name)
            .orElseThrow { Exception("Role $role_name not found") }
        
        // Add role to user's roles set
        findUser.roles.add(findRole)
        
        // Save and return updated user
        return userRepository.save(findUser)
    }

//    add one role
    @Transactional
    fun updateRoleThisUser(user_id: String, newRole: String){
        val user = userRepository.findById(user_id).orElseThrow {Exception("User not found")}
        val role = roleRepository.findById(newRole).orElseThrow{ Exception("Role not found")}
       if(user.roles.any{it.name == newRole}){
           return
       }
    user.roles.add(role)
    userRepository

    }
   //delete one role
   @Transactional
    fun removeSpecificRole(user_id: String, roleToRemove: String){
      val user = userRepository.findById(user_id).orElseThrow{ Exception("User not found") }
       val removed  = user.roles.removeIf { it.name == roleToRemove }
       if(removed){
           userRepository.save(user)
       }
    }

    //    Replace All
    @Transactional
    fun replaceAllRoles(user_id: String,newRoleName: List<String> ){
       val user = userRepository.findById(user_id).orElseThrow{ Exception("User not found") }
        // clear all roles
        user.roles.clear();
        newRoleName.forEach { name->
            val role = roleRepository.findById(name).orElse(null)
            if(role !=null) user.roles.add(role)
        }
        userRepository.save(user)
    }
    // delete all role this user
    @Transactional
    fun deleteRolesThisUser(user_id: String){
        val user = userRepository.findById(user_id).orElseThrow{ Exception(" User not found")}
       // clear all
        user.roles.clear();
        userRepository.save(user);
    }

}