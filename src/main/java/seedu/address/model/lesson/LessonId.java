package seedu.address.model.lesson;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the permanent identifier of a Lesson: a positive whole number.
 * Lesson IDs are numbered separately from student IDs.
 * Guarantees: immutable; is valid as declared in {@link #isValidId(int)}
 */
public class LessonId implements Comparable<LessonId> {

    public static final String MESSAGE_CONSTRAINTS = "Lesson ID must be a positive whole number.";

    public final int value;

    /**
     * Constructs a {@code LessonId}.
     *
     * @param value A valid ID.
     */
    public LessonId(int value) {
        checkArgument(isValidId(value), MESSAGE_CONSTRAINTS);
        this.value = value;
    }

    /**
     * Returns true if a given number is a valid ID.
     */
    public static boolean isValidId(int test) {
        return test >= 1;
    }

    @Override
    public int compareTo(LessonId other) {
        return Integer.compare(value, other.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof LessonId otherId)) {
            return false;
        }

        return value == otherId.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}
