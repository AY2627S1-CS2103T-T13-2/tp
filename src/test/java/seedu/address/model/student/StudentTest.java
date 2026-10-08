package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalClock.CURRENT_MONTH;
import static seedu.address.testutil.TypicalClock.LAST_MONTH;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.StudentBuilder;

public class StudentTest {

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Student(null, ALICE.getName(), ALICE.getContact(),
                ALICE.getLevel()));
        assertThrows(NullPointerException.class, () -> new Student(ALICE.getId(), null, ALICE.getContact(),
                ALICE.getLevel()));
        assertThrows(NullPointerException.class, () -> new Student(ALICE.getId(), ALICE.getName(), null,
                ALICE.getLevel()));
        assertThrows(NullPointerException.class, () -> new Student(ALICE.getId(), ALICE.getName(),
                ALICE.getContact(), null));
    }

    @Test
    public void getLastPaidMonth() {
        assertEquals(Optional.empty(), ALICE.getLastPaidMonth()); // never paid
        assertEquals(Optional.of(CURRENT_MONTH), BRYAN.getLastPaidMonth());
    }

    @Test
    public void isPaid_isTrueOnlyForTheMonthLastPaid() {
        assertFalse(ALICE.isPaid(CURRENT_MONTH)); // never paid
        assertTrue(BRYAN.isPaid(CURRENT_MONTH));
        assertFalse(BRYAN.isPaid(CURRENT_MONTH.plusMonths(1))); // resets by itself in a new month
        assertFalse(BRYAN.isPaid(LAST_MONTH));
    }

    @Test
    public void markPaid_returnsACopyPaidForThatMonth() {
        Student paid = ALICE.markPaid(CURRENT_MONTH);

        assertTrue(paid.isPaid(CURRENT_MONTH));
        assertFalse(ALICE.isPaid(CURRENT_MONTH)); // the original is unchanged
        assertEquals(ALICE.getId(), paid.getId());
        assertEquals(ALICE.getName(), paid.getName());
    }

    @Test
    public void isSameStudent() {
        // same object
        assertTrue(ALICE.isSameStudent(ALICE));

        // null
        assertFalse(ALICE.isSameStudent(null));

        // same name and contact, everything else different: a duplicate
        Student duplicate = new StudentBuilder(ALICE).withId(9).withLevel("Secondary 1")
                .withLastPaidMonth(CURRENT_MONTH).build();
        assertTrue(ALICE.isSameStudent(duplicate));

        // same name, different contact: a different student
        assertFalse(ALICE.isSameStudent(new StudentBuilder(ALICE).withId(9).withContact("90000000").build()));

        // different name, same contact: a different student
        assertFalse(ALICE.isSameStudent(new StudentBuilder(ALICE).withId(9).withName("Alice Lim").build()));

        // a name that differs only in letter case is a different student
        assertFalse(ALICE.isSameStudent(new StudentBuilder(ALICE).withId(9).withName("alice tan").build()));
    }

    @Test
    public void equals() {
        // same values
        assertTrue(ALICE.equals(new StudentBuilder(ALICE).build()));

        // same object
        assertTrue(ALICE.equals(ALICE));

        // null, different type
        assertFalse(ALICE.equals(null));
        assertFalse(ALICE.equals(5));

        // different student
        assertFalse(ALICE.equals(BRYAN));

        // each field matters
        assertFalse(ALICE.equals(new StudentBuilder(ALICE).withId(9).build()));
        assertFalse(ALICE.equals(new StudentBuilder(ALICE).withName("Alice Lim").build()));
        assertFalse(ALICE.equals(new StudentBuilder(ALICE).withContact("90000000").build()));
        assertFalse(ALICE.equals(new StudentBuilder(ALICE).withLevel("Primary 5").build()));
        assertFalse(ALICE.equals(new StudentBuilder(ALICE).withLastPaidMonth(CURRENT_MONTH).build()));
    }

    @Test
    public void hashCode_equalStudentsHaveEqualHashCodes() {
        assertEquals(ALICE.hashCode(), new StudentBuilder(ALICE).build().hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Student.class.getCanonicalName() + "{id=" + ALICE.getId() + ", name=" + ALICE.getName()
                + ", contact=" + ALICE.getContact() + ", level=" + ALICE.getLevel() + ", lastPaidMonth=null}";
        assertEquals(expected, ALICE.toString());
    }
}
