package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SubjectTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Subject(null));
    }

    @Test
    public void constructor_invalidSubject_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Subject(""));
    }

    @Test
    public void isValidSubject() {
        // null subject
        assertThrows(NullPointerException.class, () -> Subject.isValidSubject(null));

        // invalid subjects
        assertFalse(Subject.isValidSubject("")); // empty string
        assertFalse(Subject.isValidSubject(" ")); // spaces only
        assertFalse(Subject.isValidSubject("101")); // no letter, so it would be read as an ID
        assertFalse(Subject.isValidSubject("Maths!")); // symbol
        assertFalse(Subject.isValidSubject("O'Level Maths")); // an apostrophe is not allowed
        assertFalse(Subject.isValidSubject("a".repeat(51))); // too long

        // valid subjects
        assertTrue(Subject.isValidSubject("Mathematics"));
        assertTrue(Subject.isValidSubject("Maths 2"));
        assertTrue(Subject.isValidSubject("Add-Maths")); // hyphen
        assertTrue(Subject.isValidSubject("a"));
        assertTrue(Subject.isValidSubject("a".repeat(50))); // longest allowed
    }

    @Test
    public void equals() {
        Subject subject = new Subject("Science");

        assertTrue(subject.equals(new Subject("Science")));
        assertTrue(subject.equals(subject));
        assertFalse(subject.equals(null));
        assertFalse(subject.equals(5.0f));
        assertFalse(subject.equals(new Subject("English")));
    }
}
