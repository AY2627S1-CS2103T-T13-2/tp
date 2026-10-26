package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.EditCommand.MESSAGE_DUPLICATE_LESSON;
import static seedu.address.logic.commands.EditCommand.MESSAGE_DUPLICATE_STUDENT;
import static seedu.address.logic.commands.EditCommand.MESSAGE_LESSON_CLASH;
import static seedu.address.logic.commands.EditCommand.MESSAGE_LESSON_LEVEL_MISMATCH;
import static seedu.address.logic.commands.EditCommand.MESSAGE_NO_CHANGES;
import static seedu.address.logic.commands.EditCommand.MESSAGE_STUDENT_LEVEL_MISMATCH;
import static seedu.address.testutil.TypicalClock.CLOCK;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.SCIENCE_P6;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.LessonEdit;
import seedu.address.logic.commands.EditCommand.StudentEdit;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.StudentId;

public class EditCommandTest {

    @Test
    public void constructor_nullTarget_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EditCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.of(new Name("Alice Lee")), Optional.empty(),
                Optional.empty());

        assertThrows(NullPointerException.class, () -> new EditCommand(edit).execute(null));
    }

    @Test
    public void studentEdit_noFields_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new StudentEdit(ALICE.getId(), Optional.empty(), Optional.empty(),
                Optional.empty()));
    }

    @Test
    public void studentEdit_nullFieldContainer_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentEdit(ALICE.getId(), null, Optional.empty(),
                Optional.empty()));
    }

    @Test
    public void lessonEdit_noFields_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new LessonEdit(MATHEMATICS.getId(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
    }

    @Test
    public void lessonEdit_nullFieldContainer_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new LessonEdit(MATHEMATICS.getId(), null, Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
    }

    @Test
    public void execute_studentPartialEdit_updatesOnlySuppliedField() {
        Model model = typicalModel();
        Model expected = typicalModel();
        Name editedName = new Name("Alice Lee");
        expected.editStudent(ALICE.getId(), editedName, ALICE.getContact(), ALICE.getLevel());
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.of(editedName), Optional.empty(), Optional.empty());

        assertCommandSuccess(new EditCommand(edit), model, "Edited student: Alice Lee (ID: 1).", expected);
    }

    @Test
    public void execute_paidStudentEdit_preservesPaymentStatusAndEnrollments() {
        Model model = typicalModel();
        Model expected = typicalModel();
        Contact editedContact = new Contact("90001111");
        expected.editStudent(BRYAN.getId(), BRYAN.getName(), editedContact, BRYAN.getLevel());
        StudentEdit edit = new StudentEdit(BRYAN.getId(), Optional.empty(), Optional.of(editedContact),
                Optional.empty());

        assertCommandSuccess(new EditCommand(edit), model, "Edited student: Bryan Lim (ID: 2).", expected);
    }

    @Test
    public void execute_studentValuesAreUnchanged_reportsNoChanges() {
        Model model = typicalModel();
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.of(ALICE.getName()),
                Optional.of(ALICE.getContact()), Optional.of(ALICE.getLevel()));

        assertCommandSuccess(new EditCommand(edit), model, MESSAGE_NO_CHANGES, typicalModel());
    }

    @Test
    public void execute_studentDoesNotExist_throwsCommandException() {
        StudentId missingId = new StudentId(99);
        StudentEdit edit = new StudentEdit(missingId, Optional.of(new Name("Alice Lee")), Optional.empty(),
                Optional.empty());

        assertCommandFailure(new EditCommand(edit), typicalModel(),
                String.format(MESSAGE_STUDENT_NOT_FOUND, missingId));
    }

    @Test
    public void execute_studentBecomesDuplicate_throwsCommandException() {
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.of(BRYAN.getName()),
                Optional.of(BRYAN.getContact()), Optional.empty());

        assertCommandFailure(new EditCommand(edit), typicalModel(),
                String.format(MESSAGE_DUPLICATE_STUDENT, BRYAN.getId()));
    }

    @Test
    public void execute_studentLevelConflictsWithEnrollment_throwsCommandException() {
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.empty(), Optional.empty(),
                Optional.of(new Level("Primary 4")));

        assertCommandFailure(new EditCommand(edit), typicalModel(), MESSAGE_STUDENT_LEVEL_MISMATCH);
    }

    @Test
    public void execute_lessonPartialEdit_updatesOnlySuppliedFields() {
        Model model = typicalModel();
        Model expected = typicalModel();
        Subject editedSubject = new Subject("Additional Mathematics");
        Fee editedFee = new Fee("55");
        expected.editLesson(MATHEMATICS.getId(), editedSubject, MATHEMATICS.getLevel(), MATHEMATICS.getTimeslot(),
                editedFee);
        LessonEdit edit = new LessonEdit(MATHEMATICS.getId(), Optional.of(editedSubject), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(editedFee));

        assertCommandSuccess(new EditCommand(edit), model,
                "Edited lesson: Additional Mathematics | Primary 6 | Monday 15:00-16:30 (ID: 1).", expected);
    }

    @Test
    public void execute_onlyEndTimeSupplied_retainsStartTime() {
        Model model = typicalModel();
        Model expected = typicalModel();
        LocalTime editedEnd = LocalTime.of(17, 0);
        Timeslot editedTimeslot = new Timeslot(DayOfWeek.MONDAY, LocalTime.of(15, 0), editedEnd);
        expected.editLesson(MATHEMATICS.getId(), MATHEMATICS.getSubject(), MATHEMATICS.getLevel(), editedTimeslot,
                MATHEMATICS.getFee());
        LessonEdit edit = new LessonEdit(MATHEMATICS.getId(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.of(editedEnd), Optional.empty());

        assertCommandSuccess(new EditCommand(edit), model,
                "Edited lesson: Mathematics | Primary 6 | Monday 15:00-17:00 (ID: 1).", expected);
    }

    @Test
    public void execute_lessonValuesAreUnchanged_reportsNoChanges() {
        LessonEdit edit = new LessonEdit(MATHEMATICS.getId(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(MATHEMATICS.getFee()));

        assertCommandSuccess(new EditCommand(edit), typicalModel(), MESSAGE_NO_CHANGES, typicalModel());
    }

    @Test
    public void execute_lessonDoesNotExist_throwsCommandException() {
        LessonId missingId = new LessonId(99);
        LessonEdit edit = new LessonEdit(missingId, Optional.of(new Subject("Physics")), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        assertCommandFailure(new EditCommand(edit), typicalModel(),
                String.format(MESSAGE_LESSON_NOT_FOUND, missingId));
    }

    @Test
    public void execute_lessonBecomesDuplicate_throwsCommandException() {
        LessonEdit edit = new LessonEdit(SCIENCE_P6.getId(), Optional.of(MATHEMATICS.getSubject()),
                Optional.of(MATHEMATICS.getLevel()), Optional.of(MATHEMATICS.getTimeslot().getDay()),
                Optional.of(MATHEMATICS.getTimeslot().getStart()),
                Optional.of(MATHEMATICS.getTimeslot().getEnd()), Optional.empty());

        assertCommandFailure(new EditCommand(edit), typicalModel(),
                String.format(MESSAGE_DUPLICATE_LESSON, MATHEMATICS.getId()));
    }

    @Test
    public void execute_lessonLevelConflictsWithEnrollment_throwsCommandException() {
        LessonEdit edit = new LessonEdit(MATHEMATICS.getId(), Optional.empty(),
                Optional.of(new Level("Secondary 2")), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty());

        assertCommandFailure(new EditCommand(edit), typicalModel(), MESSAGE_LESSON_LEVEL_MISMATCH);
    }

    @Test
    public void execute_lessonClashesWithAnotherLesson_throwsCommandException() {
        LessonEdit edit = new LessonEdit(SCIENCE_P6.getId(), Optional.empty(), Optional.empty(),
                Optional.of(DayOfWeek.MONDAY), Optional.of(LocalTime.of(16, 0)),
                Optional.of(LocalTime.of(17, 0)), Optional.empty());

        assertCommandFailure(new EditCommand(edit), typicalModel(), MESSAGE_LESSON_CLASH);
    }

    @Test
    public void execute_partialTimeMakesEndNotLaterThanStart_throwsCommandException() {
        LessonEdit edit = new LessonEdit(MATHEMATICS.getId(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(LocalTime.of(17, 0)), Optional.empty(), Optional.empty());

        assertCommandFailure(new EditCommand(edit), typicalModel(), Timeslot.MESSAGE_INVALID_END_TIME);
    }

    private static Model typicalModel() {
        return new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
    }
}
