package com.localnow.professional.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(name = "professional_availability")
class ProfessionalAvailability private constructor(
    @Id
    val id: UUID,

    @Column(name = "professional_user_id", nullable = false)
    val professionalUserId: UUID,

    @Column(name = "day_of_week", nullable = false)
    val dayOfWeek: Int,

    @Column(name = "start_time", nullable = false)
    val startTime: LocalTime,

    @Column(name = "end_time", nullable = false)
    val endTime: LocalTime,
) {
    fun day(): DayOfWeek = DayOfWeek.of(dayOfWeek)

    companion object {
        fun create(
            professionalUserId: UUID,
            dayOfWeek: DayOfWeek,
            startTime: LocalTime,
            endTime: LocalTime,
        ): ProfessionalAvailability {
            require(startTime < endTime) { "Availability start time must be before end time" }

            return ProfessionalAvailability(
                id = UUID.randomUUID(),
                professionalUserId = professionalUserId,
                dayOfWeek = dayOfWeek.value,
                startTime = startTime,
                endTime = endTime,
            )
        }
    }
}
