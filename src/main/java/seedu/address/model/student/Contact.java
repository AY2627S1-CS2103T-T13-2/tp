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

    /** The most characters a contact may have, counting spaces and symbols. */
    public static final int MAX_LENGTH = 25;

    public static final String MESSAGE_CONSTRAINTS = String.format("Invalid contact. Use %d to %d digits, at most %d "
            + "characters in all. Digits can be split into groups by brackets or by a single space or hyphen, and a + "
            + "can start the number, such as +65 (9123) 4567.", MIN_DIGITS, MAX_DIGITS, MAX_LENGTH);

    /**
     * An optional plus sign, then groups of digits separated by at most one space or hyphen, where a group may be in
     * brackets, such as {@code +65 (9123) 4567} or {@code 9123-4567}. A group in brackets needs no space or hyphen
     * next to it, so {@code +65(9123)4567} is also allowed.
     */
    public static final String VALIDATION_REGEX = "\\+?(\\(\\d+\\)|\\d+)([ -]?(\\(\\d+\\)|\\d+))*";

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
     * Returns true if a given string is a valid contact number: in the form of {@link #VALIDATION_REGEX}, at most
     * {@link #MAX_LENGTH} characters, with between {@link #MIN_DIGITS} and {@link #MAX_DIGITS} digits. Other characters
     * are not counted, so {@code +65 9123 4567} has 10 digits.
     */
    public static boolean isValidContact(String test) {
        if (test.length() > MAX_LENGTH || !test.matches(VALIDATION_REGEX)) {
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
