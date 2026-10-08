package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class LessonIdTest {

    @Test
    public void constructor_notPositive_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new LessonId(0));
        assertThrows(IllegalArgumentException.class, () -> new LessonId(-1));
    }

    @Test
    public void isValidId() {
        assertFalse(LessonId.isValidId(0));
        assertFalse(LessonId.isValidId(-5));
        assertTrue(LessonId.isValidId(1));
        assertTrue(LessonId.isValidId(Integer.MAX_VALUE));
    }

    @Test
    public void compareTo_ordersByNumber() {
        assertTrue(new LessonId(2).compareTo(new LessonId(10)) < 0);
        assertTrue(new LessonId(10).compareTo(new LessonId(2)) > 0);
        assertEquals(0, new LessonId(7).compareTo(new LessonId(7)));
    }

    @Test
    public void equals() {
        LessonId id = new LessonId(12);

        assertTrue(id.equals(new LessonId(12)));
        assertTrue(id.equals(id));
        assertFalse(id.equals(null));
        assertFalse(id.equals(12)); // an int is not a LessonId
        assertFalse(id.equals(new LessonId(13)));
        assertEquals(id.hashCode(), new LessonId(12).hashCode());
    }

    @Test
    public void toString_isTheNumber() {
        assertEquals("12", new LessonId(12).toString());
    }
}
