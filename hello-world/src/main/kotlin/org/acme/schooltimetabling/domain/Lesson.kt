package org.acme.schooltimetabling.domain

import ai.timefold.solver.core.api.domain.entity.PlanningEntity
import ai.timefold.solver.core.api.domain.lookup.PlanningId
import ai.timefold.solver.core.api.domain.variable.PlanningVariable

@PlanningEntity
data class Lesson (
    @PlanningId
    val id: String,
    val subject: String,
    //  Info: problem properties are immutable once the lesson is created.
    val teacher: String,
    val studentGroup: String) {
    // Info: if a palnning variable is set(not null), it means it is solved.
    @PlanningVariable
    var timeslot: Timeslot? = null

    @PlanningVariable
    var room: Room? = null

    // No-arg constructor required for Timefold
    constructor() : this("0", "", "", "")

    override fun toString(): String = "$subject($id)"

}