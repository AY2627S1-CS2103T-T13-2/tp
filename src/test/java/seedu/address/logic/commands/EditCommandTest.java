package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.EditCommand.MESSAGE_DUPLICATE_LESSON;
import static seedu.address.logic.commands.EditCommand.MESSAGE_DUPLICATE_STUDENT;
import static seedu.address.logic.commands.EditCommand.MESSAGE_LESSON_CLASH;
import static seedu.address.logic.commands.EditCommand.MESSAGE_NOTE_FREE_LESSON;
import static seedu.address.logic.commands.EditCommand.MESSAGE_NOTE_LEVEL_MISMATCH;
import static seedu.address.logic.commands.EditCommand.MESSAGE_NOTE_SAME_NAME;
import static seedu.address.logic.commands.EditCommand.MESSAGE_NOTE_STUDENT_LEVEL_MISMATCH;
import static seedu.address.logic.commands.EditCommand.MESSAGE_NO_CHANGES;
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
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.of(new Name("Alice Lee")),
                Optional.empty(), Optional.empty());

        assertThrows(NullPointerException.class, () -> new EditCommand(edit).execute(null));
    }

    @Test
    public void studentEdit_noFields_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new StudentEdit(ALICE.getId(),
                Optional.empty(), Optional.empty(), Optional.empty()));
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
        assertThrows(NullPointerException.class, () -> new LessonEdit(MATHEMATICS.getId(), null,
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
    }

    @Test
    public void execute_studentPartialEdit_updatesOnlySuppliedField() {
        Model model = createTypicalModel();
        Model expected = createTypicalModel();
        Name editedName = new Name("Alice Lee");
        expected.editStudent(ALICE.getId(), editedName, ALICE.getContact(), ALICE.getLevel());
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.of(editedName),
                Optional.empty(), Optional.empty());

        assertCommandSuccess(new EditCommand(edit), model, "Edited student: Alice Lee (ID: 1).", expected);
    }

    @Test
    public void execute_paidStudentEdit_preservesPaymentStatusAndEnrollments() {
        Model model = createTypicalModel();
        Model expected = createTypicalModel();
        Contact editedContact = new Contact("90001111");
        expected.editStudent(BRYAN.getId(), BRYAN.getName(), editedContact, BRYAN.getLevel());
        StudentEdit edit = new StudentEdit(BRYAN.getId(), Optional.empty(), Optional.of(editedContact),
                Optional.empty());

        assertCommandSuccess(new EditCommand(edit), model, "Edited student: Bryan Lim (ID: 2).", expected);
    }

    @Test
    public void execute_studentEditedToExistingNameWithDifferentContact_addsNote() {
        Model model = createTypicalModel();
        Model expected = createTypicalModel();
        Name editedName = new Name("bryan lim");
        expected.editStudent(ALICE.getId(), editedName, ALICE.getContact(), ALICE.getLevel());
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.of(editedName),
                Optional.empty(), Optional.empty());
        String expectedMessage = "Edited student: bryan lim (ID: 1).\n"
                + String.format(MESSAGE_NOTE_SAME_NAME, BRYAN.getName(), BRYAN.getId());

        assertCommandSuccess(new EditCommand(edit), model, expectedMessage, expected);
    }

    @Test
    public void execute_studentValuesAreUnchanged_reportsNoChanges() {
        Model model = createTypicalModel();
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.of(ALICE.getName()),
                Optional.of(ALICE.getContact()), Optional.of(ALICE.getLevel()));

        assertCommandSuccess(new EditCommand(edit), model,
                CommandResult.createWithoutDataChange(MESSAGE_NO_CHANGES), createTypicalModel());
    }

    @Test
    public void execute_studentDoesNotExist_throwsCommandException() {
        StudentId missingId = new StudentId(99);
        StudentEdit edit = new StudentEdit(missingId, Optional.of(new Name("Alice Lee")), Optional.empty(),
                Optional.empty());

        assertCommandFailure(new EditCommand(edit), createTypicalModel(),
                String.format(MESSAGE_STUDENT_NOT_FOUND, missingId));
    }

    @Test
    public void execute_studentBecomesDuplicate_throwsCommandException() {
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.of(new Name("bryan lim")),
                Optional.of(new Contact("9876-5432")), Optional.empty());

        assertCommandFailure(new EditCommand(edit), createTypicalModel(),
                String.format(MESSAGE_DUPLICATE_STUDENT, BRYAN.getId()));
    }

    @Test
    public void execute_studentLevelConflictsWithEnrollments_editsAndAddsNoteForEachLesson() {
        Model model = createTypicalModel();
        Model expected = createTypicalModel();
        Level editedLevel = new Level("Primary 4");
        expected.editStudent(ALICE.getId(), ALICE.getName(), ALICE.getContact(), editedLevel);
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.empty(), Optional.empty(),
                Optional.of(editedLevel));
        String expectedMessage = "Edited student: Alice Tan (ID: 1).\n"
                + String.format(MESSAGE_NOTE_STUDENT_LEVEL_MISMATCH, ALICE.getName(), editedLevel,
                        MATHEMATICS.getId(), MATHEMATICS.getLevel()) + "\n"
                + String.format(MESSAGE_NOTE_STUDENT_LEVEL_MISMATCH, ALICE.getName(), editedLevel,
                        SCIENCE_P6.getId(), SCIENCE_P6.getLevel());

        assertCommandSuccess(new EditCommand(edit), model, expectedMessage, expected);
    }

    @Test
    public void execute_studentLevelChangesOnlyLetterCase_editsStoredLevel() throws Exception {
        Model model = createTypicalModel();
        Level editedLevel = new Level("primary 6");
        StudentEdit edit = new StudentEdit(ALICE.getId(), Optional.empty(), Optional.empty(),
                Optional.of(editedLevel));

        new EditCommand(edit).execute(model);

        assertEquals("primary 6", model.findStudent(ALICE.getId()).orElseThrow().getLevel().toString());
    }

    @Test
    public void execute_lessonPartialEdit_updatesOnlySuppliedFields() {
        Model model = createTypicalModel();
        Model expected = createTypicalModel();
        Subject editedSubject = new Subject("Additional Mathematics");
        Fee editedFee = new Fee("55");
        expected.editLesson(MATHEMATICS.getId(), editedSubject, MATHEMATICS.getLevel(),
                MATHEMATICS.getTimeslot(), editedFee);
        LessonEdit edit = new LessonEditBuilder(MATHEMATICS.getId())
                .withSubject(editedSubject)
                .withFee(editedFee)
                .build();

        assertCommandSuccess(new EditCommand(edit), model,
                "Edited lesson: Additional Mathematics | Primary 6 | Monday 15:00-16:30 (ID: 1).", expected);
    }

    @Test
    public void execute_lessonFeeChangedToZero_editsAndAddsNote() {
        Model model = createTypicalModel();
        Model expected = createTypicalModel();
        Fee free = new Fee("0");
        expected.editLesson(MATHEMATICS.getId(), MATHEMATICS.getSubject(), MATHEMATICS.getLevel(),
                MATHEMATICS.getTimeslot(), free);
        LessonEdit edit = new LessonEditBuilder(MATHEMATICS.getId()).withFee(free).build();
        String expectedMessage = "Edited lesson: Mathematics | Primary 6 | Monday 15:00-16:30 (ID: 1).\n"
                + MESSAGE_NOTE_FREE_LESSON;

        assertCommandSuccess(new EditCommand(edit), model, expectedMessage, expected);
    }

    @Test
    public void execute_onlyEndTimeSupplied_retainsStartTime() {
        Model model = createTypicalModel();
        Model expected = createTypicalModel();
        LocalTime editedEnd = LocalTime.of(17, 0);
        Timeslot editedTimeslot = new Timeslot(DayOfWeek.MONDAY, LocalTime.of(15, 0), editedEnd);
        expected.editLesson(MATHEMATICS.getId(), MATHEMATICS.getSubject(), MATHEMATICS.getLevel(),
                editedTimeslot, MATHEMATICS.getFee());
        LessonEdit edit = new LessonEditBuilder(MATHEMATICS.getId()).withEnd(editedEnd).build();

        assertCommandSuccess(new EditCommand(edit), model,
                "Edited lesson: Mathematics | Primary 6 | Monday 15:00-17:00 (ID: 1).", expected);
    }

    @Test
    public void execute_lessonValuesAreUnchanged_reportsNoChanges() {
        LessonEdit edit = new LessonEditBuilder(MATHEMATICS.getId()).withFee(MATHEMATICS.getFee()).build();

        assertCommandSuccess(new EditCommand(edit), createTypicalModel(),
                CommandResult.createWithoutDataChange(MESSAGE_NO_CHANGES), createTypicalModel());
    }

    @Test
    public void execute_lessonDoesNotExist_throwsCommandException() {
        LessonId missingId = new LessonId(99);
        LessonEdit edit = new LessonEditBuilder(missingId).withSubject(new Subject("Physics")).build();

        assertCommandFailure(new EditCommand(edit), createTypicalModel(),
                String.format(MESSAGE_LESSON_NOT_FOUND, missingId));
    }

    @Test
    public void execute_lessonBecomesDuplicate_throwsCommandException() {
        LessonEdit edit = new LessonEditBuilder(SCIENCE_P6.getId())
                .withSubject(new Subject("mAtHeMaTiCs"))
                .withLevel(MATHEMATICS.getLevel())
                .withDay(MATHEMATICS.getTimeslot().getDay())
                .withStart(MATHEMATICS.getTimeslot().getStart())
                .withEnd(MATHEMATICS.getTimeslot().getEnd())
                .build();

        assertCommandFailure(new EditCommand(edit), createTypicalModel(),
                String.format(MESSAGE_DUPLICATE_LESSON, MATHEMATICS.getId()));
    }

    @Test
    public void execute_lessonLevelConflictsWithEnrollment_editsAndAddsNote() {
        Model model = createTypicalModel();
        Model expected = createTypicalModel();
        Level editedLevel = new Level("Secondary 2");
        expected.editLesson(MATHEMATICS.getId(), MATHEMATICS.getSubject(), editedLevel,
                MATHEMATICS.getTimeslot(), MATHEMATICS.getFee());
        LessonEdit edit = new LessonEditBuilder(MATHEMATICS.getId()).withLevel(editedLevel).build();
        String expectedMessage = "Edited lesson: Mathematics | Secondary 2 | Monday 15:00-16:30 (ID: 1).\n"
                + String.format(MESSAGE_NOTE_LEVEL_MISMATCH, ALICE.getName(), ALICE.getLevel(), editedLevel);

        assertCommandSuccess(new EditCommand(edit), model, expectedMessage, expected);
    }

    @Test
    public void execute_lessonLevelChangesOnlyLetterCase_editsAndShowsNewSpelling() {
        Model model = createTypicalModel();
        Model expected = createTypicalModel();
        Level editedLevel = new Level("primary 6");
        expected.editLesson(MATHEMATICS.getId(), MATHEMATICS.getSubject(), editedLevel,
                MATHEMATICS.getTimeslot(), MATHEMATICS.getFee());
        LessonEdit edit = new LessonEditBuilder(MATHEMATICS.getId()).withLevel(editedLevel).build();

        assertCommandSuccess(new EditCommand(edit), model,
                "Edited lesson: Mathematics | primary 6 | Monday 15:00-16:30 (ID: 1).", expected);
    }

    @Test
    public void execute_lessonClashesWithAnotherLesson_throwsCommandException() {
        LessonEdit edit = new LessonEditBuilder(SCIENCE_P6.getId())
                .withDay(DayOfWeek.MONDAY)
                .withStart(LocalTime.of(16, 0))
                .withEnd(LocalTime.of(17, 0))
                .build();

        String expectedMessage = String.format(MESSAGE_LESSON_CLASH, MATHEMATICS.getId(),
                MATHEMATICS.getSubject(), MATHEMATICS.getTimeslot());
        assertCommandFailure(new EditCommand(edit), createTypicalModel(), expectedMessage);
    }

    @Test
    public void execute_endAtMidnight_throwsCommandExceptionSuggesting2359() {
        LessonEdit edit = new LessonEditBuilder(MATHEMATICS.getId()).withEnd(LocalTime.MIDNIGHT).build();

        assertCommandFailure(new EditCommand(edit), createTypicalModel(), Timeslot.MESSAGE_MIDNIGHT_END);
    }

    @Test
    public void execute_shortLessonPastMidnight_throwsCommandExceptionSuggestingSplit() {
        LessonEdit edit = new LessonEditBuilder(MATHEMATICS.getId())
                .withStart(LocalTime.of(23, 0))
                .withEnd(LocalTime.of(0, 59))
                .build();

        assertCommandFailure(new EditCommand(edit), createTypicalModel(), Timeslot.MESSAGE_END_BEFORE_START);
    }

    @Test
    public void execute_movedStartAfterStoredEnd_throwsCommandExceptionAskingForLaterEnd() {
        LessonEdit edit = new LessonEditBuilder(MATHEMATICS.getId()).withStart(LocalTime.of(17, 0)).build();

        assertCommandFailure(new EditCommand(edit), createTypicalModel(), Timeslot.MESSAGE_INVALID_END_TIME);
    }

    private static Model createTypicalModel() {
        return new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
    }

    /** Builds a lesson edit with only the fields relevant to each test. */
    private static final class LessonEditBuilder {
        private final LessonId id;
        private Optional<Subject> subject = Optional.empty();
        private Optional<Level> level = Optional.empty();
        private Optional<DayOfWeek> day = Optional.empty();
        private Optional<LocalTime> start = Optional.empty();
        private Optional<LocalTime> end = Optional.empty();
        private Optional<Fee> fee = Optional.empty();

        LessonEditBuilder(LessonId id) {
            this.id = id;
        }

        LessonEditBuilder withSubject(Subject subject) {
            this.subject = Optional.of(subject);
            return this;
        }

        LessonEditBuilder withLevel(Level level) {
            this.level = Optional.of(level);
            return this;
        }

        LessonEditBuilder withDay(DayOfWeek day) {
            this.day = Optional.of(day);
            return this;
        }

        LessonEditBuilder withStart(LocalTime start) {
            this.start = Optional.of(start);
            return this;
        }

        LessonEditBuilder withEnd(LocalTime end) {
            this.end = Optional.of(end);
            return this;
        }

        LessonEditBuilder withFee(Fee fee) {
            this.fee = Optional.of(fee);
            return this;
        }

        LessonEdit build() {
            return new LessonEdit(id, subject, level, day, start, end, fee);
        }
    }
}
