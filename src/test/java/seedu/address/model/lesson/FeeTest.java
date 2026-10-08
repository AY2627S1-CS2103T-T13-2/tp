package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class FeeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Fee(null));
    }

    @Test
    public void constructor_invalidFee_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Fee("0"));
    }

    @Test
    public void isValidFee() {
        // null fee
        assertThrows(NullPointerException.class, () -> Fee.isValidFee(null));

        // invalid fees
        assertFalse(Fee.isValidFee("")); // empty string
        assertFalse(Fee.isValidFee(" ")); // spaces only
        assertFalse(Fee.isValidFee("0")); // zero
        assertFalse(Fee.isValidFee("0.00")); // zero
        assertFalse(Fee.isValidFee("-5")); // negative
        assertFalse(Fee.isValidFee("50.001")); // three decimal places
        assertFalse(Fee.isValidFee("$50")); // currency symbol
        assertFalse(Fee.isValidFee("fifty")); // letters
        assertFalse(Fee.isValidFee("5.")); // a decimal point with no decimals
        assertFalse(Fee.isValidFee(".5")); // no digit before the decimal point
        assertFalse(Fee.isValidFee("1e3")); // scientific notation

        // valid fees
        assertTrue(Fee.isValidFee("50"));
        assertTrue(Fee.isValidFee("50.5"));
        assertTrue(Fee.isValidFee("50.00"));
        assertTrue(Fee.isValidFee("0.01")); // smallest allowed
        assertTrue(Fee.isValidFee("1234567.89"));
    }

    @Test
    public void toString_alwaysShowsTwoDecimalPlacesWithDollarSign() {
        assertEquals("$50.00", new Fee("50").toString());
        assertEquals("$50.50", new Fee("50.5").toString());
        assertEquals("$50.25", new Fee("50.25").toString());
    }

    @Test
    public void toPlainString_hasNoCurrencySymbol() {
        assertEquals("50.00", new Fee("50").toPlainString());
    }

    @Test
    public void equals_comparesTheAmountNotTheWayItWasTyped() {
        Fee fee = new Fee("50.00");

        assertTrue(fee.equals(new Fee("50")));
        assertTrue(fee.equals(new Fee("50.0")));
        assertTrue(fee.equals(fee));
        assertFalse(fee.equals(null));
        assertFalse(fee.equals(5.0f));
        assertFalse(fee.equals(new Fee("50.01")));
        assertEquals(fee.hashCode(), new Fee("50").hashCode());
    }
}
