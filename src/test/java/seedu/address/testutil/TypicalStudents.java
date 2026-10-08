package seedu.address.testutil;

import java.util.Arrays;
import java.util.List;

import seedu.address.model.student.Student;

/**
 * A utility class containing a list of {@code Student} objects to be used in tests.
 * The first three are in {@link TypicalTuiTracker}; {@code DANIEL} is not.
 */
public class TypicalStudents {

    public static final Student ALICE = new StudentBuilder().withId(1).withName("Alice Tan")
            .withContact("91234567").withLevel("Primary 6").build();
    public static final Student BRYAN = new StudentBuilder().withId(2).withName("Bryan Lim")
            .withContact("98765432").withLevel("Secondary 2").withLastPaidMonth(TypicalClock.CURRENT_MONTH).build();
    public static final Student CHLOE = new StudentBuilder().withId(3).withName("Chloe Ng")
            .withContact("81234567").withLevel("Primary 4").build();

    /** A Primary 6 student who is not in the typical TuiTracker. */
    public static final Student DANIEL = new StudentBuilder().withId(4).withName("Daniel Koh")
            .withContact("92223333").withLevel("Primary 6").build();

    private TypicalStudents() {} // prevents instantiation

    public static List<Student> getTypicalStudents() {
        return Arrays.asList(ALICE, BRYAN, CHLOE);
    }
}
