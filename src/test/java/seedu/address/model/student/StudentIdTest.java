package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudentIdTest {

    @Test
    public void constructor_notPositive_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new StudentId(0));
        assertThrows(IllegalArgumentException.class, () -> new StudentId(-1));
    }

    @Test
    public void isValidId() {
        assertFalse(StudentId.isValidId(0));
        assertFalse(StudentId.isValidId(-5));
        assertTrue(StudentId.isValidId(1));
        assertTrue(StudentId.isValidId(Integer.MAX_VALUE));
    }

    @Test
    public void compareTo_ordersByNumber() {
        assertTrue(new StudentId(2).compareTo(new StudentId(10)) < 0);
        assertTrue(new StudentId(10).compareTo(new StudentId(2)) > 0);
        assertEquals(0, new StudentId(7).compareTo(new StudentId(7)));
    }

    @Test
    public void equals() {
        StudentId id = new StudentId(12);

        assertTrue(id.equals(new StudentId(12)));
        assertTrue(id.equals(id));
        assertFalse(id.equals(null));
        assertFalse(id.equals(12)); // an int is not a StudentId
        assertFalse(id.equals(new StudentId(13)));
        assertEquals(id.hashCode(), new StudentId(12).hashCode());
    }

    @Test
    public void toString_isTheNumber() {
        assertEquals("12", new StudentId(12).toString());
    }
}
