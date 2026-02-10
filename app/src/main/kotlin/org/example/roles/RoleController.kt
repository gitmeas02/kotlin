package org.example.roles

import org.example.users.entity.Role
import org.example.users.repository.RoleRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/role")
class RoleController (
    private val roleService: RoleService
){
    // REST API for role management
    // create / read / update / delete
    @RequestMapping("/create")
    fun createRole(@RequestBody request: RoleRequest): ResponseEntity<Role>{
            val role = roleService.createRole(request.role_id, request.descript_role)
        return ResponseEntity.ok(role);
    }

}

data class RoleRequest(
    val role_id: String,
    val descript_role: String? = null
)