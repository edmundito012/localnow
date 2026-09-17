package com.localnow.professional.application

import java.time.DayOfWeek
import java.time.LocalTime

data class AvailabilitySlotCommand(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
)
