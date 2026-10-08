package seedu.address.testutil;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.lesson.Lesson;

/**
 * A utility class containing a list of {@code Lesson} objects to be used in tests.
 * The first four are in {@link TypicalTuiTracker}; {@code HISTORY} is not.
 */
public class TypicalLessons {

    public static final Lesson MATHEMATICS = new LessonBuilder().withId(1).withSubject("Mathematics")
            .withLevel("Primary 6").withTimeslot(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30))
            .withFee("50.00").build();
    public static final Lesson SCIENCE_P6 = new LessonBuilder().withId(2).withSubject("Science")
            .withLevel("Primary 6").withTimeslot(DayOfWeek.THURSDAY, LocalTime.of(17, 0), LocalTime.of(18, 0))
            .withFee("45.00").build();
    public static final Lesson SCIENCE_S2 = new LessonBuilder().withId(3).withSubject("Science")
            .withLevel("Secondary 2").withTimeslot(DayOfWeek.WEDNESDAY, LocalTime.of(16, 0), LocalTime.of(17, 30))
            .withFee("60.00").build();
    public static final Lesson ENGLISH = new LessonBuilder().withId(4).withSubject("English")
            .withLevel("Primary 4").withTimeslot(DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(11, 0))
            .withFee("40.00").build();

    /** A Primary 6 lesson that is not in the typical TuiTracker and does not clash with any lesson in it. */
    public static final Lesson HISTORY = new LessonBuilder().withId(5).withSubject("History")
            .withLevel("Primary 6").withTimeslot(DayOfWeek.FRIDAY, LocalTime.of(14, 0), LocalTime.of(15, 0))
            .withFee("35.00").build();

    private TypicalLessons() {} // prevents instantiation

    public static List<Lesson> getTypicalLessons() {
        return Arrays.asList(MATHEMATICS, SCIENCE_P6, SCIENCE_S2, ENGLISH);
    }
}
