package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalClock.CLOCK;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.getTypicalLessons;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.getTypicalStudents;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TuiTrackerBuilder;

class DeleteCommandTest {

    private final Model model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);

    @Test
    void execute_deleteStudent_removesStudentAndEnrollments() throws Exception {
        DeleteCommand command = new DeleteCommand(ALICE.getId());

        CommandResult result = command.execute(model);

        assertEquals("Deleted student: Alice Tan (ID: 1)", result.getFeedbackToUser());
        assertTrue(model.findStudent(ALICE.getId()).isEmpty());
        assertFalse(model.isEnrolled(ALICE.getId(), MATHEMATICS.getId()));
        assertTrue(model.findLesson(MATHEMATICS.getId()).isPresent());
    }

    @Test
    void execute_deleteLesson_removesLessonAndEnrollments() throws Exception {
        DeleteCommand command = new DeleteCommand(MATHEMATICS.getId());

        CommandResult result = command.execute(model);

        assertEquals("Deleted lesson: Mathematics | Primary 6 | Monday 15:00-16:30 (ID: 1)",
                result.getFeedbackToUser());
        assertTrue(model.findLesson(MATHEMATICS.getId()).isEmpty());
        assertFalse(model.isEnrolled(ALICE.getId(), MATHEMATICS.getId()));
        assertTrue(model.findStudent(ALICE.getId()).isPresent());
    }

    @Test
    void execute_missingRecordOfRequestedKind_reportsErrorWithoutMutation() {
        DeleteCommand missingStudent = new DeleteCommand(new StudentId(999));
        DeleteCommand missingLesson = new DeleteCommand(new LessonId(999));

        CommandException studentError = org.junit.jupiter.api.Assertions
                .assertThrows(CommandException.class, () -> missingStudent.execute(model));
        CommandException lessonError = org.junit.jupiter.api.Assertions
                .assertThrows(CommandException.class, () -> missingLesson.execute(model));

        assertEquals("No student found with ID 999.", studentError.getMessage());
        assertEquals("No lesson found with ID 999.", lessonError.getMessage());
        assertTrue(model.findStudent(ALICE.getId()).isPresent());
        assertTrue(model.findLesson(MATHEMATICS.getId()).isPresent());
    }

    @Test
    void execute_idExistsOnlyForOtherKind_reportsRequestedKindMissing() {
        TuiTrackerBuilder trackerBuilder = new TuiTrackerBuilder().withNextIds(9, 6);
        getTypicalStudents().forEach(trackerBuilder::withStudent);
        getTypicalLessons().forEach(trackerBuilder::withLesson);
        Model modelWithStudentIdEight = new ModelManager(trackerBuilder
                .withStudent(new StudentBuilder().withId(8).build()).build(), new UserPrefs(), CLOCK);

        CommandException studentError = org.junit.jupiter.api.Assertions
                .assertThrows(CommandException.class, () -> new DeleteCommand(new StudentId(4))
                        .execute(modelWithStudentIdEight));
        CommandException lessonError = org.junit.jupiter.api.Assertions
                .assertThrows(CommandException.class, () -> new DeleteCommand(new LessonId(8))
                        .execute(modelWithStudentIdEight));

        assertEquals("No student found with ID 4.", studentError.getMessage());
        assertEquals("No lesson found with ID 8.", lessonError.getMessage());
        assertTrue(modelWithStudentIdEight.findLesson(new LessonId(4)).isPresent());
        assertTrue(modelWithStudentIdEight.findStudent(new StudentId(8)).isPresent());
    }
}
