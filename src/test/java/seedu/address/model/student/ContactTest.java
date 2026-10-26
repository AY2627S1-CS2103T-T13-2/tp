package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class ContactTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Contact(null));
    }

    @Test
    public void constructor_invalidContact_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Contact(""));
    }

    @Test
    public void isValidContact() {
        // null contact
        assertThrows(NullPointerException.class, () -> Contact.isValidContact(null));

        // invalid contacts
        assertFalse(Contact.isValidContact("")); // empty string
        assertFalse(Contact.isValidContact(" ")); // spaces only
        assertFalse(Contact.isValidContact("91")); // too short
        assertFalse(Contact.isValidContact("1".repeat(Contact.MAX_DIGITS + 1))); // too long
        assertFalse(Contact.isValidContact("phone")); // letters
        assertFalse(Contact.isValidContact("9011p041")); // letters within digits

        // valid contacts
        assertTrue(Contact.isValidContact("1".repeat(Contact.MIN_DIGITS))); // shortest allowed
        assertTrue(Contact.isValidContact("91234567"));
        assertTrue(Contact.isValidContact("1".repeat(Contact.MAX_DIGITS))); // longest allowed
    }

    @Test
    public void constraintsMessage_namesTheLimits() {
        assertTrue(Contact.MESSAGE_CONSTRAINTS.contains(String.valueOf(Contact.MIN_DIGITS)));
        assertTrue(Contact.MESSAGE_CONSTRAINTS.contains(String.valueOf(Contact.MAX_DIGITS)));
    }

    @Test
    public void equals() {
        Contact contact = new Contact("999");

        assertTrue(contact.equals(new Contact("999")));
        assertTrue(contact.equals(contact));
        assertFalse(contact.equals(null));
        assertFalse(contact.equals(5.0f));
        assertFalse(contact.equals(new Contact("995")));
    }

    @Test
    public void digits_keepsOnlyDigits() {
        assertEquals("92223333", new Contact("92223333").digits());
    }

    @Test
    public void isSameContact_sameDigits_returnsTrue() {
        assertTrue(new Contact("92223333").isSameContact(new Contact("92223333")));
        assertFalse(new Contact("92223333").isSameContact(new Contact("92223334")));
    }

    @Test
    public void isValidContact_formattedNumbers() {
        assertTrue(Contact.isValidContact("9222 3333"));
        assertTrue(Contact.isValidContact("+65 (9222) 3333"));
        assertTrue(Contact.isValidContact("9222-3333"));
        assertTrue(Contact.isValidContact("1-2-3"));
        assertFalse(Contact.isValidContact("+-()"));
        assertFalse(Contact.isValidContact("92 22 33 33 92 22 33 33")); // 16 digits
        assertFalse(Contact.isValidContact("9222 3333 (HP)"));
    }

    @Test
    public void isSameContact_differentFormatting_returnsTrue() {
        assertTrue(new Contact("9222 3333").isSameContact(new Contact("92223333")));
        assertTrue(new Contact("+65 9222-3333").isSameContact(new Contact("6592223333")));
    }
}
