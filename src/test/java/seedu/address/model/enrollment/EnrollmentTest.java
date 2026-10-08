package seedu.address.model.enrollment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEnrollments.ALICE_MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.SCIENCE_P6;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;

import org.junit.jupiter.api.Test;

public class EnrollmentTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Enrollment(null, MATHEMATICS.getId()));
        assertThrows(NullPointerException.class, () -> new Enrollment(ALICE.getId(), null));
    }

    @Test
    public void equals() {
        assertTrue(ALICE_MATHEMATICS.equals(new Enrollment(ALICE.getId(), MATHEMATICS.getId())));
        assertTrue(ALICE_MATHEMATICS.equals(ALICE_MATHEMATICS));
        assertFalse(ALICE_MATHEMATICS.equals(null));
        assertFalse(ALICE_MATHEMATICS.equals(5));
        assertFalse(ALICE_MATHEMATICS.equals(new Enrollment(BRYAN.getId(), MATHEMATICS.getId())));
        assertFalse(ALICE_MATHEMATICS.equals(new Enrollment(ALICE.getId(), SCIENCE_P6.getId())));
        assertEquals(ALICE_MATHEMATICS.hashCode(), new Enrollment(ALICE.getId(), MATHEMATICS.getId()).hashCode());
    }
}
