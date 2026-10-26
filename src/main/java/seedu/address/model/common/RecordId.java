package seedu.address.model.common;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * The permanent identifier of a record: a positive whole number.
 * Each kind of record has its own subclass, so a student ID is never equal to a lesson ID even when the numbers
 * are the same.
 * Guarantees: immutable; is valid as declared in {@link #isValidId(int)}
 *
 * @param <T> the subclass, so that IDs can only be compared with IDs of the same kind.
 */
public abstract class RecordId<T extends RecordId<T>> implements Comparable<T> {

    public final int value;

    /**
     * Constructs a {@code RecordId}.
     *
     * @param value A valid ID.
     * @param constraintsMessage The message to report if {@code value} is not a valid ID.
     */
    protected RecordId(int value, String constraintsMessage) {
        checkArgument(isValidId(value), constraintsMessage);
        this.value = value;
    }

    /**
     * Returns true if a given number is a valid ID.
     */
    public static boolean isValidId(int test) {
        return test >= 1;
    }

    @Override
    public int compareTo(T other) {
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

        // comparing classes also handles nulls, and keeps IDs of different kinds apart
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        return value == ((RecordId<?>) other).value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}
