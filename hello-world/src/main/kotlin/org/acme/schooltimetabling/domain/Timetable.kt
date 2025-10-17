package org.acme.schooltimetabling.domain

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty
import ai.timefold.solver.core.api.domain.solution.PlanningScore
import ai.timefold.solver.core.api.domain.solution.PlanningSolution
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import ai.timefold.solver.core.api.solver.SolverStatus
// Info: PlanningSolution contins all of the input data.
@PlanningSolution
data class Timetable (
    // Info: @PlanningEntityCollectionProperty and @ProblemFactCollectionProperty are used to let TimetableConstraintProvider select from those instances in order to calculate the score.
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    // Info: ProblemFact is the static input data.
    val timeslots: List<Timeslot>,
    @ProblemFactCollectionProperty
    // Info: ValueRangeProvider: the possible values the solver can assign to the entities
    @ValueRangeProvider
    val rooms: List<Room>,
    // Info: PlanningEntity is dynamic, changesduring solving.
    // Info: ValueRangeProvider makes rooms available as a named range called by the id ofthe field rooms
    @PlanningEntityCollectionProperty
    val lessons: List<Lesson>,
    // Info: infeasible solution means hard constraint violations.
    // Info: feasible solution means no hard constraint violations(only soft constraint violations).
    @PlanningScore
    var score: HardSoftScore? = null) {

    // No-arg constructor required for Timefold
    constructor() : this(emptyList(), emptyList(), emptyList())
}
