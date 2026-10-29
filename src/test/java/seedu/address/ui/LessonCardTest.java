package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;

import java.util.List;

import org.junit.jupiter.api.Test;

public class LessonCardTest {
    @Test
    public void enrolledStudentsText_noStudents_saysNone() {
        assertEquals("Enrolled students (ID and name): none", LessonCard.enrolledStudentsText(List.of()));
    }

    @Test
    public void enrolledStudentsText_oneStudent_showsTheIdBeforeTheName() {
        assertEquals("Enrolled students (ID and name): 1 (Alice Tan)",
                LessonCard.enrolledStudentsText(List.of(ALICE)));
    }

    @Test
    public void enrolledStudentsText_severalStudents_separatesThemWithCommas() {
        assertEquals("Enrolled students (ID and name): 1 (Alice Tan), 2 (Bryan Lim)",
                LessonCard.enrolledStudentsText(List.of(ALICE, BRYAN)));
    }
}
