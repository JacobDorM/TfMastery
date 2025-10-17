package org.acme.schooltimetabling

import ai.timefold.solver.core.api.solver.SolverFactory
import ai.timefold.solver.core.config.solver.SolverConfig
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime
import java.util.Objects
import java.util.function.Function
import java.util.stream.Collectors
import org.acme.schooltimetabling.domain.Lesson
import org.acme.schooltimetabling.domain.Room
import org.acme.schooltimetabling.domain.Timeslot
import org.acme.schooltimetabling.domain.Timetable
import org.acme.schooltimetabling.solver.TimetableConstraintProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object TimetableApp {
        private val LOGGER: Logger = LoggerFactory.getLogger(TimetableApp::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
                val solverFactory =
                        SolverFactory.create<Timetable>(
                                SolverConfig()
                                        .withSolutionClass(Timetable::class.java)
                                        .withEntityClasses(Lesson::class.java)
                                        .withConstraintProviderClass(
                                                TimetableConstraintProvider::class.java
                                        )
                                        // The solver runs only for 5 seconds on this small dataset.
                                        // It's recommended to run for at least 5 minutes ("5m")
                                        // otherwise.
                                        .withTerminationSpentLimit(Duration.ofSeconds(5))
                        )

                // Load the problem
                val problem = generateDemoData()

                // Solve the problem
                val solver = solverFactory.buildSolver()
                val solution = solver.solve(problem)

                // Visualize the solution
                printTimetable(solution)
        }

        fun generateDemoData(): Timetable {
                val timeslots: MutableList<Timeslot> = ArrayList(10)
                timeslots.add(Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)))
                timeslots.add(Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)))
                timeslots.add(
                        Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30))
                )
                timeslots.add(
                        Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30))
                )
                timeslots.add(
                        Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30))
                )

                timeslots.add(Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)))
                timeslots.add(
                        Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30))
                )
                timeslots.add(
                        Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30))
                )
                timeslots.add(
                        Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30))
                )
                timeslots.add(
                        Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30))
                )

                val rooms: MutableList<Room> = ArrayList(3)
                rooms.add(Room("Room A"))
                rooms.add(Room("Room B"))
                rooms.add(Room("Room C"))

                val lessons: MutableList<Lesson> = ArrayList()
                var nextLessonId = 0L
                lessons.add(Lesson(nextLessonId++.toString(), "Math", "A. Turing", "9th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "Math", "A. Turing", "9th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "Physics", "M. Curie", "9th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "Chemistry", "M. Curie", "9th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "Biology", "C. Darwin", "9th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "History", "I. Jones", "9th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "English", "I. Jones", "9th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "English", "I. Jones", "9th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "Spanish", "P. Cruz", "9th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "Spanish", "P. Cruz", "9th grade"))

                lessons.add(Lesson(nextLessonId++.toString(), "Math", "A. Turing", "10th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "Math", "A. Turing", "10th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "Math", "A. Turing", "10th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "Physics", "M. Curie", "10th grade"))
                lessons.add(
                        Lesson(nextLessonId++.toString(), "Chemistry", "M. Curie", "10th grade")
                )
                lessons.add(Lesson(nextLessonId++.toString(), "French", "M. Curie", "10th grade"))
                lessons.add(
                        Lesson(nextLessonId++.toString(), "Geography", "C. Darwin", "10th grade")
                )
                lessons.add(Lesson(nextLessonId++.toString(), "History", "I. Jones", "10th grade"))
                lessons.add(Lesson(nextLessonId++.toString(), "English", "P. Cruz", "10th grade"))
                lessons.add(Lesson(nextLessonId.toString(), "Spanish", "P. Cruz", "10th grade"))

                return Timetable(timeslots, rooms, lessons)
        }

        private fun printTimetable(timeTable: Timetable) {
                LOGGER.info("")
                val rooms = timeTable.rooms
                val lessons = timeTable.lessons
                val lessonMap =
                        lessons.stream()
                                .filter { lesson: Lesson ->
                                        lesson.timeslot != null && lesson.room != null
                                }
                                .collect(
                                        Collectors.groupingBy(
                                                Function<Lesson, Timeslot> { it.timeslot!! },
                                                Collectors.groupingBy(
                                                        Function<Lesson, Room> { it.room!! }
                                                )
                                        )
                                )
                LOGGER.info(
                        "|            | " +
                                rooms.stream()
                                        .map { room: Room -> String.format("%-10s", room.name) }
                                        .collect(Collectors.joining(" | ")) +
                                " |"
                )
                LOGGER.info("|" + "------------|".repeat(rooms.size + 1))
                for (timeslot in timeTable.timeslots) {
                        val cells =
                                rooms.stream()
                                        .map { room: Room ->
                                                val byRoomMap =
                                                        lessonMap[timeslot]
                                                                ?: return@map emptyList<Lesson>()
                                                val cellLessons =
                                                        byRoomMap[room] ?: emptyList<Lesson>()
                                                Objects.requireNonNullElse(cellLessons, emptyList())
                                        }
                                        .toList()

                        LOGGER.info(
                                "| " +
                                        String.format(
                                                "%-10s",
                                                timeslot.dayOfWeek.toString().substring(0, 3) +
                                                        " " +
                                                        timeslot.startTime
                                        ) +
                                        " | " +
                                        cells.stream()
                                                .map { cellLessons: List<Lesson> ->
                                                        String.format(
                                                                "%-10s",
                                                                cellLessons
                                                                        .stream()
                                                                        .map { obj: Lesson ->
                                                                                obj.subject
                                                                        }
                                                                        .collect(
                                                                                Collectors.joining(
                                                                                        ", "
                                                                                )
                                                                        )
                                                        )
                                                }
                                                .collect(Collectors.joining(" | ")) +
                                        " |"
                        )
                        LOGGER.info(
                                "|            | " +
                                        cells.stream()
                                                .map { cellLessons: List<Lesson> ->
                                                        String.format(
                                                                "%-10s",
                                                                cellLessons
                                                                        .stream()
                                                                        .map { obj: Lesson ->
                                                                                obj.teacher
                                                                        }
                                                                        .collect(
                                                                                Collectors.joining(
                                                                                        ", "
                                                                                )
                                                                        )
                                                        )
                                                }
                                                .collect(Collectors.joining(" | ")) +
                                        " |"
                        )
                        LOGGER.info(
                                "|            | " +
                                        cells.stream()
                                                .map { cellLessons: List<Lesson> ->
                                                        String.format(
                                                                "%-10s",
                                                                cellLessons
                                                                        .stream()
                                                                        .map { obj: Lesson ->
                                                                                obj.studentGroup
                                                                        }
                                                                        .collect(
                                                                                Collectors.joining(
                                                                                        ", "
                                                                                )
                                                                        )
                                                        )
                                                }
                                                .collect(Collectors.joining(" | ")) +
                                        " |"
                        )
                        LOGGER.info("|" + "------------|".repeat(rooms.size + 1))
                }
                val unassignedLessons =
                        lessons.stream()
                                .filter { lesson: Lesson ->
                                        lesson.timeslot == null || lesson.room == null
                                }
                                .toList()
                if (!unassignedLessons.isEmpty()) {
                        LOGGER.info("")
                        LOGGER.info("Unassigned lessons")
                        for (lesson in unassignedLessons) {
                                LOGGER.info(
                                        "  " +
                                                lesson.subject +
                                                " - " +
                                                lesson.teacher +
                                                " - " +
                                                lesson.studentGroup
                                )
                        }
                }
        }
}
