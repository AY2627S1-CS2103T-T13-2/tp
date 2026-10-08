package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand.ExistingLesson;
import seedu.address.logic.commands.AddCommand.ExistingStudent;
import seedu.address.logic.commands.AddCommand.LessonRef;
import seedu.address.logic.commands.AddCommand.NewLesson;
import seedu.address.logic.commands.AddCommand.NewStudent;
import seedu.address.logic.commands.AddCommand.StudentRef;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.StudentId;

public class AddCommandTest {

    private static final StudentRef EXISTING_STUDENT = new ExistingStudent(new StudentId(12));
    private static final StudentRef NEW_STUDENT =
            new NewStudent(new Name("Alice Tan"), new Contact("91234567"), new Level("Primary 6"));
    private static final LessonRef EXISTING_LESSON = new ExistingLesson(new LessonId(7));
    private static final Timeslot MONDAY_AFTERNOON =
            new Timeslot(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30));
    private static final LessonRef NEW_LESSON =
            new NewLesson(new Subject("Mathematics"), new Level("Primary 6"), MONDAY_AFTERNOON, new Fee("50.00"));

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null, EXISTING_LESSON));
        assertThrows(NullPointerException.class, () -> new AddCommand(EXISTING_STUDENT, null));
    }

    @Test
    public void references_nullDetails_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ExistingStudent(null));
        assertThrows(NullPointerException.class, () -> new ExistingLesson(null));
        assertThrows(NullPointerException.class, () -> new NewStudent(null, new Contact("91234567"),
                new Level("Primary 6")));
        assertThrows(NullPointerException.class, () -> new NewStudent(new Name("Alice Tan"), null,
                new Level("Primary 6")));
        assertThrows(NullPointerException.class, () -> new NewStudent(new Name("Alice Tan"),
                new Contact("91234567"), null));
        assertThrows(NullPointerException.class, () -> new NewLesson(null, new Level("Primary 6"),
                MONDAY_AFTERNOON, new Fee("50")));
        assertThrows(NullPointerException.class, () -> new NewLesson(new Subject("Mathematics"), null,
                MONDAY_AFTERNOON, new Fee("50")));
        assertThrows(NullPointerException.class, () -> new NewLesson(new Subject("Mathematics"),
                new Level("Primary 6"), null, new Fee("50")));
        assertThrows(NullPointerException.class, () -> new NewLesson(new Subject("Mathematics"),
                new Level("Primary 6"), MONDAY_AFTERNOON, null));
    }

    @Test
    public void equals() {
        AddCommand command = new AddCommand(NEW_STUDENT, NEW_LESSON);

        // same values
        assertTrue(command.equals(new AddCommand(NEW_STUDENT, NEW_LESSON)));

        // same object
        assertTrue(command.equals(command));

        // null, different type
        assertFalse(command.equals(null));
        assertFalse(command.equals(5));

        // different student, or different lesson
        assertFalse(command.equals(new AddCommand(EXISTING_STUDENT, NEW_LESSON)));
        assertFalse(command.equals(new AddCommand(NEW_STUDENT, EXISTING_LESSON)));
        assertFalse(command.equals(new AddCommand(new ExistingStudent(new StudentId(13)), NEW_LESSON)));
        assertFalse(command.equals(new AddCommand(NEW_STUDENT,
                new NewLesson(new Subject("Science"), new Level("Primary 6"), MONDAY_AFTERNOON, new Fee("50.00")))));
    }

    @Test
    public void hashCode_equalCommandsHaveEqualHashCodes() {
        assertEquals(new AddCommand(NEW_STUDENT, NEW_LESSON).hashCode(),
                new AddCommand(NEW_STUDENT, NEW_LESSON).hashCode());
    }

    @Test
    public void toStringMethod() {
        AddCommand command = new AddCommand(EXISTING_STUDENT, NEW_LESSON);
        String expected = AddCommand.class.getCanonicalName() + "{student=" + EXISTING_STUDENT + ", lesson="
                + NEW_LESSON + "}";
        assertEquals(expected, command.toString());
    }
}
