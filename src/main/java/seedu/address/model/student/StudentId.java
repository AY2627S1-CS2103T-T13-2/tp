package seedu.address.model.student;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the permanent identifier of a Student: a positive whole number.
 * Student IDs are numbered separately from lesson IDs.
 * Guarantees: immutable; is valid as declared in {@link #isValidId(int)}
 */
public class StudentId implements Comparable<StudentId> {

    public static final String MESSAGE_CONSTRAINTS = "Student ID must be a positive whole number.";

    public final int value;

    /**
     * Constructs a {@code StudentId}.
     *
     * @param value A valid ID.
     */
    public StudentId(int value) {
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
    public int compareTo(StudentId other) {
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
        if (!(other instanceof StudentId otherId)) {
            return false;
        }

        return value == otherId.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}
