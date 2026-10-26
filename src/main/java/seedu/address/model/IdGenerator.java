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

    private int nextStudentId;
    private int nextLessonId;

    /**
     * Creates a generator whose first student ID and first lesson ID are both 1.
     */
    public IdGenerator() {
        this(FIRST_ID, FIRST_ID);
    }

    /**
     * Creates a generator that will hand out the given IDs next.
     */
    public IdGenerator(int nextStudentId, int nextLessonId) {
        checkArgument(StudentId.isValidId(nextStudentId), StudentId.MESSAGE_CONSTRAINTS);
        checkArgument(LessonId.isValidId(nextLessonId), LessonId.MESSAGE_CONSTRAINTS);
        this.nextStudentId = nextStudentId;
        this.nextLessonId = nextLessonId;
    }

    /**
     * Returns a new student ID and moves the counter on.
     */
    public StudentId newStudentId() {
        return new StudentId(nextStudentId++);
    }

    /**
     * Returns a new lesson ID and moves the counter on.
     */
    public LessonId newLessonId() {
        return new LessonId(nextLessonId++);
    }

    public int getNextStudentId() {
        return nextStudentId;
    }

    public int getNextLessonId() {
        return nextLessonId;
    }

    /**
     * Sets both counters to exactly the given values.
     */
    public void reset(int nextStudentId, int nextLessonId) {
        checkArgument(StudentId.isValidId(nextStudentId), StudentId.MESSAGE_CONSTRAINTS);
        checkArgument(LessonId.isValidId(nextLessonId), LessonId.MESSAGE_CONSTRAINTS);
        this.nextStudentId = nextStudentId;
        this.nextLessonId = nextLessonId;
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
