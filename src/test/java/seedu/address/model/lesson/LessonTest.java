package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.SCIENCE_P6;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.LessonBuilder;

public class LessonTest {

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Lesson(null, MATHEMATICS.getSubject(),
                MATHEMATICS.getLevel(), MATHEMATICS.getTimeslot(), MATHEMATICS.getFee()));
        assertThrows(NullPointerException.class, () -> new Lesson(MATHEMATICS.getId(), null,
                MATHEMATICS.getLevel(), MATHEMATICS.getTimeslot(), MATHEMATICS.getFee()));
        assertThrows(NullPointerException.class, () -> new Lesson(MATHEMATICS.getId(), MATHEMATICS.getSubject(),
                null, MATHEMATICS.getTimeslot(), MATHEMATICS.getFee()));
        assertThrows(NullPointerException.class, () -> new Lesson(MATHEMATICS.getId(), MATHEMATICS.getSubject(),
                MATHEMATICS.getLevel(), null, MATHEMATICS.getFee()));
        assertThrows(NullPointerException.class, () -> new Lesson(MATHEMATICS.getId(), MATHEMATICS.getSubject(),
                MATHEMATICS.getLevel(), MATHEMATICS.getTimeslot(), null));
    }

    @Test
    public void isSameLesson() {
        // same object
        assertTrue(MATHEMATICS.isSameLesson(MATHEMATICS));

        // null
        assertFalse(MATHEMATICS.isSameLesson(null));

        // same subject, level and timeslot, but a different ID and fee: a duplicate
        assertTrue(MATHEMATICS.isSameLesson(new LessonBuilder(MATHEMATICS).withId(9).withFee("99.00").build()));

        // each of subject, level and timeslot makes it a different lesson
        assertFalse(MATHEMATICS.isSameLesson(new LessonBuilder(MATHEMATICS).withId(9).withSubject("Science")
                .build()));
        assertFalse(MATHEMATICS.isSameLesson(new LessonBuilder(MATHEMATICS).withId(9).withLevel("Primary 5")
                .build()));
        assertFalse(MATHEMATICS.isSameLesson(new LessonBuilder(MATHEMATICS).withId(9)
                .withTimeslot(DayOfWeek.TUESDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)).build()));
        assertFalse(MATHEMATICS.isSameLesson(new LessonBuilder(MATHEMATICS).withId(9)
                .withTimeslot(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 0)).build()));

        // a level that differs only in case and spacing is the same level
        assertTrue(MATHEMATICS.isSameLesson(new LessonBuilder(MATHEMATICS).withId(9).withLevel("primary   6")
                .build()));
    }

    @Test
    public void equals() {
        // same values
        assertTrue(MATHEMATICS.equals(new LessonBuilder(MATHEMATICS).build()));

        // same object
        assertTrue(MATHEMATICS.equals(MATHEMATICS));

        // null, different type
        assertFalse(MATHEMATICS.equals(null));
        assertFalse(MATHEMATICS.equals(5));

        // different lesson
        assertFalse(MATHEMATICS.equals(SCIENCE_P6));

        // each field matters
        assertFalse(MATHEMATICS.equals(new LessonBuilder(MATHEMATICS).withId(9).build()));
        assertFalse(MATHEMATICS.equals(new LessonBuilder(MATHEMATICS).withSubject("Science").build()));
        assertFalse(MATHEMATICS.equals(new LessonBuilder(MATHEMATICS).withLevel("Primary 5").build()));
        assertFalse(MATHEMATICS.equals(new LessonBuilder(MATHEMATICS)
                .withTimeslot(DayOfWeek.TUESDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)).build()));
        assertFalse(MATHEMATICS.equals(new LessonBuilder(MATHEMATICS).withFee("51.00").build()));
    }

    @Test
    public void hashCode_equalLessonsHaveEqualHashCodes() {
        assertEquals(MATHEMATICS.hashCode(), new LessonBuilder(MATHEMATICS).build().hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Lesson.class.getCanonicalName() + "{id=" + MATHEMATICS.getId() + ", subject="
                + MATHEMATICS.getSubject() + ", level=" + MATHEMATICS.getLevel() + ", timeslot="
                + MATHEMATICS.getTimeslot() + ", fee=" + MATHEMATICS.getFee() + "}";
        assertEquals(expected, MATHEMATICS.toString());
    }
}
