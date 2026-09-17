package com.localnow.professional.domain

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "professional_profile")
class ProfessionalProfile private constructor(
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: UUID,

    @Column(name = "display_name", nullable = false, length = 120)
    val displayName: String,

    @Column(nullable = false, unique = true, length = 16)
    val phone: String,

    @Column(length = 1000)
    val bio: String?,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "professional_service_category",
        joinColumns = [JoinColumn(name = "professional_user_id")],
    )
    @Column(name = "category_code", nullable = false, length = 50)
    val categoryCodes: MutableSet<String>,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,
) {
    fun replaceCategories(codes: Set<String>, now: Instant = Instant.now()) {
        categoryCodes.clear()
        categoryCodes.addAll(codes)
        updatedAt = now
    }

    companion object {
        fun create(
            userId: UUID,
            displayName: String,
            phone: String,
            bio: String?,
            now: Instant = Instant.now(),
        ): ProfessionalProfile {
            val normalizedName = displayName.trim()
            val normalizedPhone = phone.trim()
            val normalizedBio = bio?.trim()?.takeIf { it.isNotEmpty() }

            require(normalizedName.isNotBlank()) { "Display name must not be blank" }
            require(normalizedPhone.isNotBlank()) { "Phone must not be blank" }

            return ProfessionalProfile(
                userId = userId,
                displayName = normalizedName,
                phone = normalizedPhone,
                bio = normalizedBio,
                categoryCodes = mutableSetOf(),
                createdAt = now,
                updatedAt = now,
            )
        }
    }
}
