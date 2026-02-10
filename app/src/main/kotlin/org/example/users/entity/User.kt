package org.example.users.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
open class User(

    @Id
    @Column(nullable = false, unique = true, length = 36)
    open var uuid: String = UUID.randomUUID().toString(),
    
    @Column(nullable = false, length = 100)
    open var name: String = "",

    @Column(nullable = false, unique = true)
    open var email: String = "",

    @Column(nullable = false)
    open var password: String = "",

    @Column(nullable = true)
    open var avatar: String? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_uuid")],
        inverseJoinColumns = [JoinColumn(name = "role_name")]
    )
    open var roles: MutableSet<Role> = mutableSetOf(),

    @Column(name = "token_version", nullable = false)
    open var tokenVersion: Int = 1,

    @Column(name = "created_at", nullable = false)
    open var createdAt: Instant = Instant.now(),
    
    @Column(name = "updated_at", nullable = false)
    open var updatedAt: Instant = Instant.now()
) {
    // Example of custom getter/setter (optional)
    var safeName: String
        get() = name.uppercase()
        set(value) {
            name = value.trim()
        }

    var safeEmail: String
        get() = email.lowercase()
        set(value) {
            email = value.trim()
        }
}
