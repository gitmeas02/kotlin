package org.example.users.controller

import org.example.users.ApiResponse
import org.example.users.dto.UserCreateRequest
import org.example.users.entity.User
import org.example.users.service.UserService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {
    
    @PostMapping("/create",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody request: UserCreateRequest): ResponseEntity<ApiResponse<User>> {
        val name = request.name
        val email = request.email
        if(name.isBlank() || email.isBlank()){
            return ResponseEntity.badRequest().body(
                ApiResponse(false,"Name and Email cannot be blank")
            );
        }
        if(!email.contains("@")){
            return ResponseEntity.badRequest().body(
                ApiResponse(false, "Invalide email format")
            );
        }
        if(userService.emailExists(email)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse(false,"Choose another emails")
            )
        }
        return try{
            val createdUser = userService.createUser(name,email);
            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse(true, "User created successfully", createdUser)
            )
        }catch (e:Exception){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse(false,"An Error occured: ${e.message}"));
        }
    }
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllUsers(
        @RequestParam(defaultValue = "0") page:Int,
        @RequestParam(defaultValue = "10") size:Int
    ): ResponseEntity<Any> {
        return try {
            val users = userService.getAllUsers(
                page,size
            )
            ResponseEntity.ok(users)
        } catch (e: Exception) {
            // Log the exception for debugging
            println("Error getting all users: ${e.message}")
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                mapOf("message" to "Server error")
            )
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Map<String,String>>{
        return try{ 
            val deleted = userService.deleteUser(id)
            if (!deleted) {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    mapOf("message" to "User with id $id not found")
                )
            } else {
                ResponseEntity.status(HttpStatus.OK).body(
                    mapOf("message" to "User with id $id deleted successfully")
                )
            }
        }catch(e:Exception){
            // Log the exception for debugging
            println("Error deleting user with id $id: ${e.message}")
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
            body(
                mapOf("message" to "An error occurred: ${e.message}")
            )
        }
    }
    @GetMapping("/find/{id}",produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findUserById(@PathVariable id:Long): ResponseEntity<Any>{
        return try{
          val exist = userService.findUserById(id);
          if (exist == null) {
              ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                  mapOf("message" to "User with id $id not found")
              )
          } else {
              ResponseEntity.ok(mapOf("name" to exist.name , "email" to exist.email))
          }
        }catch(e:Exception){
          // Log the exception for debugging
          println("Error finding user by id $id: ${e.message}")
          e.printStackTrace()
          ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
           mapOf("message" to "Server error")
          )
        }

    }
}