package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

public class IdGeneratorTest {

    @Test
    public void newIds_startAtOne_andCountSeparately() {
        IdGenerator generator = new IdGenerator();

        assertEquals(new StudentId(1), generator.newStudentId());
        assertEquals(new StudentId(2), generator.newStudentId());
        assertEquals(new LessonId(1), generator.newLessonId()); // lessons have their own counter
        assertEquals(new StudentId(3), generator.newStudentId());
        assertEquals(new LessonId(2), generator.newLessonId());
        assertEquals(4, generator.getNextStudentId());
        assertEquals(3, generator.getNextLessonId());
    }

    @Test
    public void constructor_invalidCounter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new IdGenerator(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new IdGenerator(1, 0));
    }

    @Test
    public void constructor_startsFromTheGivenIds() {
        IdGenerator generator = new IdGenerator(12, 7);

        assertEquals(new StudentId(12), generator.newStudentId());
        assertEquals(new LessonId(7), generator.newLessonId());
    }

    @Test
    public void raiseTo_neverLowersACounter() {
        IdGenerator generator = new IdGenerator(10, 10);

        generator.raiseTo(5, 20);

        assertEquals(10, generator.getNextStudentId());
        assertEquals(20, generator.getNextLessonId());
    }

    @Test
    public void reset_setsBothCounters() {
        IdGenerator generator = new IdGenerator(10, 10);

        generator.reset(2, 3);

        assertEquals(2, generator.getNextStudentId());
        assertEquals(3, generator.getNextLessonId());
        assertThrows(IllegalArgumentException.class, () -> generator.reset(0, 1));
    }

    @Test
    public void equals() {
        IdGenerator generator = new IdGenerator(2, 3);

        assertTrue(generator.equals(new IdGenerator(2, 3)));
        assertTrue(generator.equals(generator));
        assertFalse(generator.equals(null));
        assertFalse(generator.equals(5));
        assertFalse(generator.equals(new IdGenerator(3, 3)));
        assertFalse(generator.equals(new IdGenerator(2, 4)));
        assertEquals(generator.hashCode(), new IdGenerator(2, 3).hashCode());
    }
}
