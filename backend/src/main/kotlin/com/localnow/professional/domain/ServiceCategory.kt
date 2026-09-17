package com.localnow.professional.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "service_category")
class ServiceCategory private constructor(
    @Id
    @Column(length = 50)
    val code: String,

    @Column(name = "display_name", nullable = false, length = 120)
    val displayName: String,

    @Column(nullable = false)
    val active: Boolean,
)
