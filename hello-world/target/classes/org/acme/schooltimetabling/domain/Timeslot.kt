package org.acme.schooltimetabling.domain

import java.time.DayOfWeek
import java.time.LocalTime

// Info: problem fact resource
data class Timeslot(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime) {

    override fun toString(): String = "$dayOfWeek $startTime"

}