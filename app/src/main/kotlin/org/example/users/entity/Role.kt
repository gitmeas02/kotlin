package org.example.users.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "roles")
open class Role(
    @Id
    @Column(nullable = false, unique = true, length = 50)
    open var name: String = "user",

    @Column(nullable = true, length = 200)
    open var description: String? = null
){
    @ManyToMany(mappedBy = "roles")
    open var users: MutableSet<User> = mutableSetOf()
}
