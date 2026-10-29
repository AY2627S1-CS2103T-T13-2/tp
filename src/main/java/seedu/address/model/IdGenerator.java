package seedu.address.model;

import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

/**
 * Hands out the next ID for a new student or a new lesson.
 * Students and lessons have separate counters. A counter only ever goes up, so an ID is never reused,
 * even after the record it belonged to is deleted.
 */
public class IdGenerator {

    /** The ID of the first student and of the first lesson. */
    public static final int FIRST_ID = 1;

    /**
     * The value of a counter once its last ID, {@link Integer#MAX_VALUE}, has been handed out. The counters are
     * {@code long} so that they can hold this value instead of wrapping around to a negative number.
     */
    public static final long ALL_IDS_USED = (long) Integer.MAX_VALUE + 1;

    private static final String MESSAGE_NO_ID_LEFT = "Every ID up to " + Integer.MAX_VALUE + " has been used.";

    private long nextStudentId;
    private long nextLessonId;

    /**
     * Creates a generator whose first student ID and first lesson ID are both 1.
     */
    public IdGenerator() {
        this(FIRST_ID, FIRST_ID);
    }

    /**
     * Creates a generator that will hand out the given IDs next.
     */
    public IdGenerator(long nextStudentId, long nextLessonId) {
        reset(nextStudentId, nextLessonId);
    }

    /**
     * Returns a new student ID and moves the counter on.
     *
     * @throws IllegalStateException if every student ID has been used.
     */
    public StudentId newStudentId() {
        if (!hasStudentIdLeft()) {
            throw new IllegalStateException(MESSAGE_NO_ID_LEFT);
        }
        return new StudentId((int) nextStudentId++);
    }

    /**
     * Returns a new lesson ID and moves the counter on.
     *
     * @throws IllegalStateException if every lesson ID has been used.
     */
    public LessonId newLessonId() {
        if (!hasLessonIdLeft()) {
            throw new IllegalStateException(MESSAGE_NO_ID_LEFT);
        }
        return new LessonId((int) nextLessonId++);
    }

    public boolean hasStudentIdLeft() {
        return nextStudentId < ALL_IDS_USED;
    }

    public boolean hasLessonIdLeft() {
        return nextLessonId < ALL_IDS_USED;
    }

    public long getNextStudentId() {
        return nextStudentId;
    }

    public long getNextLessonId() {
        return nextLessonId;
    }

    /**
     * Sets both counters to exactly the given values. A counter can be {@link #ALL_IDS_USED}, but no higher.
     */
    public void reset(long nextStudentId, long nextLessonId) {
        checkArgument(isValidCounter(nextStudentId), StudentId.MESSAGE_CONSTRAINTS);
        checkArgument(isValidCounter(nextLessonId), LessonId.MESSAGE_CONSTRAINTS);
        this.nextStudentId = nextStudentId;
        this.nextLessonId = nextLessonId;
    }

    private static boolean isValidCounter(long counter) {
        return counter >= FIRST_ID && counter <= ALL_IDS_USED;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof IdGenerator otherGenerator)) {
            return false;
        }

        return nextStudentId == otherGenerator.nextStudentId && nextLessonId == otherGenerator.nextLessonId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nextStudentId, nextLessonId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nextStudentId", nextStudentId)
                .add("nextLessonId", nextLessonId)
                .toString();
    }

}
