package com.localnow.identity.domain

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "app_user")
class User private constructor(
    @Id
    val id: UUID,

    @Column(nullable = false, unique = true, length = 320)
    val email: String,

    @Column(name = "password_hash", nullable = false, length = 255)
    val passwordHash: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 32)
    val roles: MutableSet<UserRole>,

    @Column(nullable = false)
    var enabled: Boolean,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,
) {
    fun addRole(role: UserRole, now: Instant = Instant.now()) {
        if (roles.add(role)) {
            updatedAt = now
        }
    }

    companion object {
        fun create(
            email: String,
            passwordHash: String,
            roles: Set<UserRole> = setOf(UserRole.CUSTOMER),
            now: Instant = Instant.now(),
        ): User {
            val normalizedEmail = email.trim().lowercase()

            require(normalizedEmail.isNotBlank()) { "Email must not be blank" }
            require(passwordHash.isNotBlank()) { "Password hash must not be blank" }
            require(roles.isNotEmpty()) { "A user must have at least one role" }

            return User(
                id = UUID.randomUUID(),
                email = normalizedEmail,
                passwordHash = passwordHash,
                roles = roles.toMutableSet(),
                enabled = true,
                createdAt = now,
                updatedAt = now,
            )
        }
    }
}
