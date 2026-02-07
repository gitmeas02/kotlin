package org.example.users.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
open class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0,
    
    @Column(nullable = false, length = 100)
    open var name: String = "",

    @Column(nullable = false, unique = true)
    open var email: String = "",

    @Column(nullable = false)
    open var password: String = "",

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
