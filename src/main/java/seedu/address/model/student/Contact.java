package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Student's contact number.
 * Guarantees: immutable; is valid as declared in {@link #isValidContact(String)}
 */
public class Contact {

    /** The accepted length range. Kept as constants because the exact range is still to be confirmed. */
    public static final int MIN_DIGITS = 3;
    public static final int MAX_DIGITS = 15;

    public static final String MESSAGE_CONSTRAINTS = String.format(
            "Invalid contact. Contact must contain between %d and %d digits.", MIN_DIGITS, MAX_DIGITS);

    public static final String VALIDATION_REGEX = "\\d{" + MIN_DIGITS + "," + MAX_DIGITS + "}";

    public final String value;

    /**
     * Constructs a {@code Contact}.
     *
     * @param contact A valid contact number.
     */
    public Contact(String contact) {
        requireNonNull(contact);
        checkArgument(isValidContact(contact), MESSAGE_CONSTRAINTS);
        value = contact;
    }

    /**
     * Returns true if a given string is a valid contact number.
     */
    public static boolean isValidContact(String test) {
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
        if (!(other instanceof Contact otherContact)) {
            return false;
        }

        return value.equals(otherContact.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
