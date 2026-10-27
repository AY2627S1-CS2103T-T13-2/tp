package seedu.address.model.lesson;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.BigDecimal;

/**
 * Represents the fee charged for one session of a Lesson.
 * The fee is greater than zero and has at most two decimal places. It is displayed as, for example, {@code $50.00}.
 * Guarantees: immutable; is valid as declared in {@link #isValidFee(String)}
 */
public class Fee {

    /** A fee is kept to the cent, so it has this many digits after the decimal point. */
    public static final int DECIMAL_PLACES = 2;

    /** The largest number of digits allowed before the decimal point. */
    public static final int MAX_WHOLE_DIGITS = 7;

    public static final String MESSAGE_CONSTRAINTS = "Invalid fee. Enter an amount of 0 or more, with at most "
            + MAX_WHOLE_DIGITS + " digits before the decimal point and at most two decimal places, such as 50 or "
            + "45.50.";

    /** Bounded digits with an optional decimal point and one or two decimal places. */
    public static final String VALIDATION_REGEX = "^\\d{1," + MAX_WHOLE_DIGITS + "}(\\.\\d{1,"
            + DECIMAL_PLACES + "})?$";

    private static final String CURRENCY_SYMBOL = "$";

    private final BigDecimal amount;

    /**
     * Constructs a {@code Fee}.
     *
     * @param fee A valid fee, e.g. "50", "50.5" or "50.00".
     */
    public Fee(String fee) {
        requireNonNull(fee);
        checkArgument(isValidFee(fee), MESSAGE_CONSTRAINTS);
        amount = new BigDecimal(fee).setScale(DECIMAL_PLACES);
    }

    /**
     * Returns true if a given string is a valid fee.
     */
    public static boolean isValidFee(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if the fee is 0, for a lesson that is meant to be free.
     */
    public boolean isFree() {
        return amount.signum() == 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the amount with exactly two decimal places and no currency symbol, e.g. {@code 50.00}.
     * This is the form used in the data file.
     */
    public String toPlainString() {
        return amount.toPlainString();
    }

    @Override
    public String toString() {
        return CURRENCY_SYMBOL + amount.toPlainString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Fee otherFee)) {
            return false;
        }

        return amount.equals(otherFee.amount);
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

}
