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

    public static final String MESSAGE_CONSTRAINTS = String.format("Invalid contact. Use %d to %d digits. "
            + "Spaces, +, -, ( and ) are also allowed, such as +65 9123 4567.", MIN_DIGITS, MAX_DIGITS);

    /** The characters a contact may contain: digits, spaces, plus signs, hyphens and brackets. */
    public static final String VALIDATION_REGEX = "[0-9+()\\- ]+";

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
     * Returns true if a given string is a valid contact number: only the characters allowed by
     * {@link #VALIDATION_REGEX}, with between {@link #MIN_DIGITS} and {@link #MAX_DIGITS} digits. Other characters
     * are not counted, so {@code +65 9123 4567} has 10 digits.
     */
    public static boolean isValidContact(String test) {
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }
        long digitCount = test.chars().filter(Character::isDigit).count();
        return digitCount >= MIN_DIGITS && digitCount <= MAX_DIGITS;
    }

    /**
     * Returns only the digits of this contact, in order. For example, {@code +65 9222-3333} gives
     * {@code 6592223333}.
     */
    public String digits() {
        return value.replaceAll("[^0-9]", "");
    }

    /**
     * Returns true if {@code other} has the same digits, ignoring spaces and symbols.
     * This decides whether two students are likely to be the same person. {@link #equals(Object)} stays exact, so
     * that reformatting a number still counts as an edit.
     */
    public boolean isSameContact(Contact other) {
        requireNonNull(other);
        return digits().equals(other.digits());
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
