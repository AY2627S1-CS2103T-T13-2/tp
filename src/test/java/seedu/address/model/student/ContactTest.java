package seedu.address.model.student;

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
        assertFalse(Contact.isValidContact("9312 1534")); // spaces within digits
        assertFalse(Contact.isValidContact("+6591234567")); // a plus sign
        assertFalse(Contact.isValidContact("9123-4567")); // a hyphen

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
}
