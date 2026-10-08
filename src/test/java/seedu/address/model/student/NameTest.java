package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Name(""));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("12345")); // no letter, so it would be read as an ID
        assertFalse(Name.isValidName("- '")); // no letter
        assertFalse(Name.isValidName("peter*")); // contains a symbol
        assertFalse(Name.isValidName("Tan Ah Kow Jr.")); // a full stop is not allowed
        assertFalse(Name.isValidName("a".repeat(101))); // too long

        // valid name
        assertTrue(Name.isValidName("peter jack"));
        assertTrue(Name.isValidName("Alice Tan"));
        assertTrue(Name.isValidName("O'Brien")); // apostrophe
        assertTrue(Name.isValidName("Mary-Jane")); // hyphen
        assertTrue(Name.isValidName("Alice 2nd")); // numbers are allowed if there is also a letter
        assertTrue(Name.isValidName("a")); // one letter
        assertTrue(Name.isValidName("a".repeat(100))); // longest allowed
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        assertTrue(name.equals(new Name("Valid Name")));
        assertTrue(name.equals(name));
        assertFalse(name.equals(null));
        assertFalse(name.equals(5.0f));
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
