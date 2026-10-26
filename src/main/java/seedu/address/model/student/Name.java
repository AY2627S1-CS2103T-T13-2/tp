package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.regex.Pattern;

import seedu.address.commons.util.StringUtil;

/**
 * Represents a Student's name.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MAX_LENGTH = 100;

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid student name. A name has 1 to " + MAX_LENGTH + " characters: letters, numbers, spaces and "
            + ". , ' - ( ) & @, with at least one letter. To refer to an existing student, use their ID.";

    /**
     * Must contain at least one letter, and may contain letters, numbers, spaces and the symbols . , ' - ( ) & @.
     */
    public static final String VALIDATION_REGEX =
            "^(?=.*\\p{L})[\\p{L}\\p{N} '\\-.,()&@]{1," + MAX_LENGTH + "}$";

    /**
     * The words "s/o" (son of) and "d/o" (daughter of), in any letter case, standing on their own. They are part of
     * many legal names, so their slash is allowed even though "/" is not allowed anywhere else.
     */
    private static final Pattern RELATION_WORD = Pattern.compile("(?i)(?<=^|\\s)([sd])/(o)(?=\\s|$)");

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        // Replacing the slash of each relation word with a letter keeps the length the same for the length limit.
        String withoutRelationSlashes = RELATION_WORD.matcher(test).replaceAll("$1x$2");
        return withoutRelationSlashes.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if {@code other} is the same name, ignoring letter case and extra spaces.
     * This decides whether two students are likely to be the same person. {@link #equals(Object)} stays exact, so
     * that a change in letter case still counts as an edit.
     */
    public boolean isSameName(Name other) {
        requireNonNull(other);
        return StringUtil.toMatchingKey(fullName).equals(StringUtil.toMatchingKey(other.fullName));
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
