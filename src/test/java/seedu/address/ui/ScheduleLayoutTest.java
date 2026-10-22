package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.SCIENCE_S2;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.testutil.LessonBuilder;

class ScheduleLayoutTest {

    @Test
    void groupByDay_includesAllDaysAndSortsByStartTimeThenId() {
        Lesson earlier = new LessonBuilder().withId(10)
                .withTimeslot(DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 0)).build();
        Lesson sameTime = new LessonBuilder(MATHEMATICS).withId(11).build();

        Map<DayOfWeek, List<Lesson>> result = ScheduleLayout.groupByDay(
                List.of(sameTime, SCIENCE_S2, MATHEMATICS, earlier));

        assertEquals(7, result.size());
        assertEquals(List.of(earlier, MATHEMATICS, sameTime), result.get(DayOfWeek.MONDAY));
        assertEquals(List.of(SCIENCE_S2), result.get(DayOfWeek.WEDNESDAY));
        assertTrue(result.get(DayOfWeek.SUNDAY).isEmpty());
    }

    @Test
    void overlappingLessonIds_flagsBothLessonsButNotBackToBackLesson() {
        Lesson backToBack = new LessonBuilder().withId(10)
                .withTimeslot(DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 0)).build();
        Lesson overlapping = new LessonBuilder().withId(11)
                .withTimeslot(DayOfWeek.MONDAY, LocalTime.of(15, 30), LocalTime.of(17, 0)).build();

        Set<LessonId> result = ScheduleLayout.overlappingLessonIds(
                ScheduleLayout.groupByDay(List.of(backToBack, MATHEMATICS, overlapping)));

        assertEquals(Set.of(MATHEMATICS.getId(), overlapping.getId()), result);
    }

    @Test
    void overlappingLessonIds_unsortedLessonsOnlyFlagsActualOverlaps() {
        Lesson late = new LessonBuilder().withId(10)
                .withTimeslot(DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(18, 0)).build();
        Lesson early = new LessonBuilder().withId(11)
                .withTimeslot(DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 0)).build();
        Lesson overlapsEarly = new LessonBuilder().withId(12)
                .withTimeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)).build();

        Set<LessonId> result = ScheduleLayout.overlappingLessonIds(
                Map.of(DayOfWeek.MONDAY, List.of(late, early, overlapsEarly)));

        assertEquals(Set.of(early.getId(), overlapsEarly.getId()), result);
    }
}
