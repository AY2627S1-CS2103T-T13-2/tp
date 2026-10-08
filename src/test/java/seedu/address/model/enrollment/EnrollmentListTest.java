package seedu.address.model.enrollment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEnrollments.ALICE_MATHEMATICS;
import static seedu.address.testutil.TypicalEnrollments.ALICE_SCIENCE_P6;
import static seedu.address.testutil.TypicalEnrollments.BRYAN_SCIENCE_S2;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.SCIENCE_P6;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalStudents.CHLOE;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

public class EnrollmentListTest {

    private final EnrollmentList list = new EnrollmentList();

    @Test
    public void contains() {
        assertFalse(list.contains(ALICE.getId(), MATHEMATICS.getId()));

        list.add(ALICE_MATHEMATICS);

        assertTrue(list.contains(ALICE.getId(), MATHEMATICS.getId()));
        assertFalse(list.contains(ALICE.getId(), SCIENCE_P6.getId())); // a different lesson
        assertFalse(list.contains(BRYAN.getId(), MATHEMATICS.getId())); // a different student
    }

    @Test
    public void add_sameEnrollmentTwice_throwsIllegalArgumentException() {
        list.add(ALICE_MATHEMATICS);

        assertThrows(IllegalArgumentException.class, EnrollmentList.MESSAGE_DUPLICATE_ENROLLMENT, () ->
                list.add(new Enrollment(ALICE.getId(), MATHEMATICS.getId())));
    }

    @Test
    public void removeAllOfStudent() {
        list.add(ALICE_MATHEMATICS);
        list.add(ALICE_SCIENCE_P6);
        list.add(BRYAN_SCIENCE_S2);

        list.removeAllOfStudent(ALICE.getId());

        assertEquals(Collections.singletonList(BRYAN_SCIENCE_S2), list.asUnmodifiableList());
    }

    @Test
    public void removeAllOfLesson() {
        list.add(ALICE_MATHEMATICS);
        list.add(ALICE_SCIENCE_P6);
        list.add(new Enrollment(CHLOE.getId(), MATHEMATICS.getId()));

        list.removeAllOfLesson(MATHEMATICS.getId());

        assertEquals(Collections.singletonList(ALICE_SCIENCE_P6), list.asUnmodifiableList());
    }

    @Test
    public void lessonIdsOf_isInAscendingOrder() {
        list.add(ALICE_SCIENCE_P6);
        list.add(ALICE_MATHEMATICS);
        list.add(BRYAN_SCIENCE_S2);

        assertEquals(Arrays.asList(MATHEMATICS.getId(), SCIENCE_P6.getId()), list.lessonIdsOf(ALICE.getId()));
        assertEquals(Collections.emptyList(), list.lessonIdsOf(CHLOE.getId()));
    }

    @Test
    public void studentIdsIn_isInAscendingOrder() {
        list.add(new Enrollment(CHLOE.getId(), MATHEMATICS.getId()));
        list.add(ALICE_MATHEMATICS);

        assertEquals(Arrays.asList(ALICE.getId(), CHLOE.getId()), list.studentIdsIn(MATHEMATICS.getId()));
        assertEquals(Collections.emptyList(), list.studentIdsIn(SCIENCE_P6.getId()));
    }

    @Test
    public void setEnrollments_replacesContents_andRejectsRepeats() {
        list.add(BRYAN_SCIENCE_S2);

        list.setEnrollments(Arrays.asList(ALICE_SCIENCE_P6, ALICE_MATHEMATICS));

        assertEquals(Arrays.asList(ALICE_MATHEMATICS, ALICE_SCIENCE_P6), list.asUnmodifiableList());
        assertThrows(IllegalArgumentException.class, () ->
                list.setEnrollments(Arrays.asList(ALICE_MATHEMATICS, ALICE_MATHEMATICS)));
    }

    @Test
    public void asUnmodifiableList_modifyList_throwsUnsupportedOperationException() {
        list.add(ALICE_MATHEMATICS);
        assertThrows(UnsupportedOperationException.class, () -> list.asUnmodifiableList().clear());
    }

    @Test
    public void equals_ignoresOrder() {
        EnrollmentList other = new EnrollmentList();
        list.add(ALICE_MATHEMATICS);
        list.add(BRYAN_SCIENCE_S2);
        other.add(BRYAN_SCIENCE_S2);
        other.add(ALICE_MATHEMATICS);

        assertTrue(list.equals(other));
        assertEquals(list.hashCode(), other.hashCode());
        assertFalse(list.equals(null));
        assertFalse(list.equals(new EnrollmentList()));
    }
}
