package seedu.address.model.common;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents the education level of a student or a lesson, e.g. "Primary 6".
 * Leading and trailing spaces are removed and repeated spaces are collapsed into one.
 * Two levels are equal if they are the same ignoring letter case, so "Primary 3" equals "primary   3"
 * but "P3" does not equal "Primary 3".
 * Guarantees: immutable; is valid as declared in {@link #isValidLevel(String)}
 */
public class Level {

    public static final int MAX_LENGTH = 30;

    public static final String MESSAGE_CONSTRAINTS_STUDENT = "Invalid student level. "
            + "Level must contain letters or numbers and cannot exceed " + MAX_LENGTH + " characters.";

    public static final String MESSAGE_CONSTRAINTS_LESSON = "Invalid lesson level. "
            + "Level must contain letters or numbers and cannot exceed " + MAX_LENGTH + " characters.";

    /** Letters, numbers, spaces and hyphens only, with at least one letter or number. */
    private static final String VALIDATION_REGEX = "^(?=.*[\\p{L}\\p{N}])[\\p{L}\\p{N} \\-]+$";

    public final String value;

    /**
     * Constructs a {@code Level}.
     *
     * @param level A valid level.
     */
    public Level(String level) {
        requireNonNull(level);
        checkArgument(isValidLevel(level), MESSAGE_CONSTRAINTS_STUDENT);
        value = normalise(level);
    }

    /**
     * Returns true if a given string is a valid level.
     * Leading and trailing spaces are ignored and repeated spaces count as one.
     */
    public static boolean isValidLevel(String test) {
        requireNonNull(test);
        String normalised = normalise(test);
        return normalised.length() <= MAX_LENGTH && normalised.matches(VALIDATION_REGEX);
    }

    private static String normalise(String level) {
        return level.trim().replaceAll("\\s+", " ");
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
        if (!(other instanceof Level otherLevel)) {
            return false;
        }

        return value.equalsIgnoreCase(otherLevel.value);
    }

    @Override
    public int hashCode() {
        return value.toLowerCase(Locale.ROOT).hashCode();
    }

}
