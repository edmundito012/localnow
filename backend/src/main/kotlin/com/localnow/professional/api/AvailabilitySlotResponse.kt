package com.localnow.professional.api

import java.time.DayOfWeek
import java.time.LocalTime

data class AvailabilitySlotResponse(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
)
