package seedu.address.model.lesson;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the subject of a Lesson, e.g. "Mathematics".
 * Guarantees: immutable; is valid as declared in {@link #isValidSubject(String)}
 */
public class Subject {

    public static final int MAX_LENGTH = 50;

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid lesson. Enter an existing lesson ID or a valid subject.";

    /** Must contain at least one letter, and may contain letters, numbers, spaces and hyphens. */
    public static final String VALIDATION_REGEX = "^(?=.*\\p{L})[\\p{L}\\p{N} \\-]{1," + MAX_LENGTH + "}$";

    public final String value;

    /**
     * Constructs a {@code Subject}.
     *
     * @param subject A valid subject.
     */
    public Subject(String subject) {
        requireNonNull(subject);
        checkArgument(isValidSubject(subject), MESSAGE_CONSTRAINTS);
        value = subject;
    }

    /**
     * Returns true if a given string is a valid subject.
     */
    public static boolean isValidSubject(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Subject otherSubject)) {
            return false;
        }

        return value.equals(otherSubject.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
