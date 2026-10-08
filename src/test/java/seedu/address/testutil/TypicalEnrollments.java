package seedu.address.testutil;

import static seedu.address.testutil.TypicalLessons.ENGLISH;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.SCIENCE_P6;
import static seedu.address.testutil.TypicalLessons.SCIENCE_S2;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalStudents.CHLOE;

import java.util.Arrays;
import java.util.List;

import seedu.address.model.enrollment.Enrollment;

/**
 * A utility class containing a list of {@code Enrollment} objects to be used in tests.
 * Every student is enrolled only in lessons of their own level.
 */
public class TypicalEnrollments {

    public static final Enrollment ALICE_MATHEMATICS = new Enrollment(ALICE.getId(), MATHEMATICS.getId());
    public static final Enrollment ALICE_SCIENCE_P6 = new Enrollment(ALICE.getId(), SCIENCE_P6.getId());
    public static final Enrollment BRYAN_SCIENCE_S2 = new Enrollment(BRYAN.getId(), SCIENCE_S2.getId());
    public static final Enrollment CHLOE_ENGLISH = new Enrollment(CHLOE.getId(), ENGLISH.getId());

    private TypicalEnrollments() {} // prevents instantiation

    public static List<Enrollment> getTypicalEnrollments() {
        return Arrays.asList(ALICE_MATHEMATICS, ALICE_SCIENCE_P6, BRYAN_SCIENCE_S2, CHLOE_ENGLISH);
    }
}
