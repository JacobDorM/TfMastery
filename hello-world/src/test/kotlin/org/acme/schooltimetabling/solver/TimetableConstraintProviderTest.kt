package org.acme.schooltimetabling.solver

import ai.timefold.solver.core.api.score.stream.ConstraintFactory
import ai.timefold.solver.test.api.score.stream.ConstraintVerifier
import java.time.DayOfWeek
import java.time.LocalTime
import org.acme.schooltimetabling.domain.Lesson
import org.acme.schooltimetabling.domain.Room
import org.acme.schooltimetabling.domain.Timeslot
import org.acme.schooltimetabling.domain.Timetable
import org.junit.jupiter.api.Test

internal class TimetableConstraintProviderTest {
    var constraintVerifier: ConstraintVerifier<TimetableConstraintProvider, Timetable> =
            ConstraintVerifier.build(
                    TimetableConstraintProvider(),
                    Timetable::class.java,
                    Lesson::class.java
            )

    @Test
    fun roomConflict() {
        val firstLesson =
                Lesson("1", "Subject1", "Teacher1", "Group1").apply {
                    timeslot = TIMESLOT1
                    room = ROOM1
                }
        val conflictingLesson =
                Lesson("2", "Subject2", "Teacher2", "Group2").apply {
                    timeslot = TIMESLOT1
                    room = ROOM1
                }
        val nonConflictingLesson =
                Lesson("3", "Subject3", "Teacher3", "Group3").apply {
                    timeslot = TIMESLOT2
                    room = ROOM1
                }
        constraintVerifier
                .verifyThat { obj: TimetableConstraintProvider, constraintFactory: ConstraintFactory
                    ->
                    obj.roomConflict(constraintFactory)
                }
                .given(firstLesson, conflictingLesson, nonConflictingLesson)
                .penalizesBy(1)
    }

    companion object {
        private val ROOM1 = Room("Room1")
        private val TIMESLOT1 = Timeslot(DayOfWeek.MONDAY, LocalTime.NOON, LocalTime.of(13, 0))
        private val TIMESLOT2 = Timeslot(DayOfWeek.TUESDAY, LocalTime.NOON, LocalTime.of(13, 0))
    }
}
