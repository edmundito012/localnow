package com.localnow.professional.application

import com.localnow.professional.domain.ProfessionalAvailability
import com.localnow.professional.persistence.ProfessionalAvailabilityRepository
import com.localnow.professional.persistence.ProfessionalProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DateTimeException
import java.time.ZoneId
import java.util.UUID

@Service
class ReplaceProfessionalAvailabilityService(
    private val professionalProfileRepository: ProfessionalProfileRepository,
    private val professionalAvailabilityRepository: ProfessionalAvailabilityRepository,
) {

    @Transactional
    fun replace(
        userId: UUID,
        timeZone: String,
        requestedSlots: List<AvailabilitySlotCommand>,
    ): ProfessionalAvailabilityConfiguration {
        val normalizedZone = validateTimeZone(timeZone)
        validateSlots(requestedSlots)

        val profile = professionalProfileRepository.findById(userId)
            .orElseThrow { ProfessionalProfileNotFoundException() }

        val slots = requestedSlots
            .distinct()
            .sortedWith(compareBy({ it.dayOfWeek.value }, { it.startTime }))
            .map {
                ProfessionalAvailability.create(
                    professionalUserId = userId,
                    dayOfWeek = it.dayOfWeek,
                    startTime = it.startTime,
                    endTime = it.endTime,
                )
            }

        profile.setAvailabilityTimeZone(normalizedZone)
        professionalAvailabilityRepository.deleteAllByProfessionalUserId(userId)
        professionalAvailabilityRepository.saveAll(slots)

        return ProfessionalAvailabilityConfiguration(normalizedZone, slots)
    }

    @Transactional(readOnly = true)
    fun get(userId: UUID): ProfessionalAvailabilityConfiguration {
        val profile = professionalProfileRepository.findById(userId)
            .orElseThrow { ProfessionalProfileNotFoundException() }
        val timeZone = profile.timeZone
            ?: throw InvalidAvailabilityException("Professional availability is not configured")
        val slots = professionalAvailabilityRepository
            .findAllByProfessionalUserIdOrderByDayOfWeekAscStartTimeAsc(userId)

        return ProfessionalAvailabilityConfiguration(timeZone, slots)
    }

    private fun validateTimeZone(timeZone: String): String =
        try {
            ZoneId.of(timeZone.trim()).id
        } catch (_: DateTimeException) {
            throw InvalidAvailabilityException("Unknown time zone: " + timeZone)
        }

    private fun validateSlots(slots: List<AvailabilitySlotCommand>) {
        slots.forEach {
            if (it.startTime >= it.endTime) {
                throw InvalidAvailabilityException(
                    "Availability start time must be before end time",
                )
            }
        }

        slots.groupBy { it.dayOfWeek }.forEach { (day, daySlots) ->
            val sorted = daySlots.distinct().sortedBy { it.startTime }
            sorted.zipWithNext().forEach { (current, next) ->
                if (next.startTime < current.endTime) {
                    throw InvalidAvailabilityException(
                        "Availability slots overlap on " + day.name,
                    )
                }
            }
        }
    }
}
