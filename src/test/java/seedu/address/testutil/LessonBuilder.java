package seedu.address.testutil;

import java.time.DayOfWeek;
import java.time.LocalTime;

import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;

/**
 * A utility class to help with building Lesson objects.
 */
public class LessonBuilder {

    public static final int DEFAULT_ID = 1;
    public static final String DEFAULT_SUBJECT = "Mathematics";
    public static final String DEFAULT_LEVEL = "Primary 6";
    public static final DayOfWeek DEFAULT_DAY = DayOfWeek.MONDAY;
    public static final LocalTime DEFAULT_START = LocalTime.of(15, 0);
    public static final LocalTime DEFAULT_END = LocalTime.of(16, 30);
    public static final String DEFAULT_FEE = "50.00";

    private LessonId id;
    private Subject subject;
    private Level level;
    private Timeslot timeslot;
    private Fee fee;

    /**
     * Creates a {@code LessonBuilder} with the default details.
     */
    public LessonBuilder() {
        id = new LessonId(DEFAULT_ID);
        subject = new Subject(DEFAULT_SUBJECT);
        level = new Level(DEFAULT_LEVEL);
        timeslot = new Timeslot(DEFAULT_DAY, DEFAULT_START, DEFAULT_END);
        fee = new Fee(DEFAULT_FEE);
    }

    /**
     * Initializes the LessonBuilder with the data of {@code lessonToCopy}.
     */
    public LessonBuilder(Lesson lessonToCopy) {
        id = lessonToCopy.getId();
        subject = lessonToCopy.getSubject();
        level = lessonToCopy.getLevel();
        timeslot = lessonToCopy.getTimeslot();
        fee = lessonToCopy.getFee();
    }

    /**
     * Sets the {@code LessonId} of the {@code Lesson} that we are building.
     */
    public LessonBuilder withId(int id) {
        this.id = new LessonId(id);
        return this;
    }

    /**
     * Sets the {@code Subject} of the {@code Lesson} that we are building.
     */
    public LessonBuilder withSubject(String subject) {
        this.subject = new Subject(subject);
        return this;
    }

    /**
     * Sets the {@code Level} of the {@code Lesson} that we are building.
     */
    public LessonBuilder withLevel(String level) {
        this.level = new Level(level);
        return this;
    }

    /**
     * Sets the {@code Timeslot} of the {@code Lesson} that we are building.
     */
    public LessonBuilder withTimeslot(DayOfWeek day, LocalTime start, LocalTime end) {
        this.timeslot = new Timeslot(day, start, end);
        return this;
    }

    /**
     * Sets the {@code Fee} of the {@code Lesson} that we are building.
     */
    public LessonBuilder withFee(String fee) {
        this.fee = new Fee(fee);
        return this;
    }

    public Lesson build() {
        return new Lesson(id, subject, level, timeslot, fee);
    }

}
