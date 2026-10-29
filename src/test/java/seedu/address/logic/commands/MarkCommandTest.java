package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalLessons.HISTORY;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.SCIENCE_S2;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalStudents.CHLOE;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;
import seedu.address.testutil.TypicalClock;

public class MarkCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), TypicalClock.CLOCK);
        expectedModel = new ModelManager(model.getTuiTracker(), new UserPrefs(), TypicalClock.CLOCK);
    }

    @Test
    public void execute_existingStudent_marksPaidForCurrentMonth() {
        expectedModel.markPaid(ALICE.getId());
        MarkCommand command = new MarkCommand(ALICE.getId());
        String expectedMessage = String.format(MarkCommand.MESSAGE_SUCCESS, ALICE.getName());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_missingStudent_throwsCommandException() {
        StudentId missingId = new StudentId(999);
        MarkCommand command = new MarkCommand(missingId);

        assertCommandFailure(command, model, String.format(MESSAGE_STUDENT_NOT_FOUND, missingId));
    }

    @Test
    public void execute_alreadyPaidStudent_throwsCommandException() {
        MarkCommand command = new MarkCommand(BRYAN.getId());
        String expectedMessage = String.format(MarkCommand.MESSAGE_ALREADY_PAID, BRYAN.getName());

        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void execute_multipleUnpaidStudents_marksAllPaid() {
        expectedModel.markPaid(ALICE.getId());
        expectedModel.markPaid(CHLOE.getId());
        MarkCommand command = new MarkCommand(Arrays.asList(ALICE.getId(), CHLOE.getId()));
        String expectedMessage = String.format(MarkCommand.MESSAGE_SUCCESS,
                ALICE.getName().fullName + ", " + CHLOE.getName().fullName);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleStudentsWithSomeAlreadyPaid_marksUnpaidAndNotesAlreadyPaid() {
        expectedModel.markPaid(ALICE.getId());
        MarkCommand command = new MarkCommand(Arrays.asList(ALICE.getId(), BRYAN.getId()));
        String expectedMessage = String.format(MarkCommand.MESSAGE_SUCCESS_WITH_ALREADY_PAID,
                ALICE.getName().fullName, BRYAN.getName().fullName);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleStudentsAllAlreadyPaid_throwsCommandException() {
        model.markPaid(ALICE.getId());
        MarkCommand command = new MarkCommand(Arrays.asList(ALICE.getId(), BRYAN.getId()));
        assertCommandFailure(command, model, MarkCommand.MESSAGE_ALL_ALREADY_PAID);
    }

    @Test
    public void execute_multipleStudentsOneMissing_throwsCommandExceptionWithoutMutating() {
        StudentId missingId = new StudentId(999);
        MarkCommand command = new MarkCommand(Arrays.asList(ALICE.getId(), missingId));

        assertCommandFailure(command, model, String.format(MESSAGE_STUDENT_NOT_FOUND, missingId));
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_lessonAllUnpaid_marksEnrolledStudents() {
        expectedModel.markPaid(ALICE.getId());
        MarkCommand command = new MarkCommand(MATHEMATICS.getId());
        String expectedMessage = String.format(MarkCommand.MESSAGE_SUCCESS, ALICE.getName().fullName);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_lessonSomeAlreadyPaid_marksUnpaidAndNotesAlreadyPaid() {
        model.enroll(BRYAN.getId(), MATHEMATICS.getId());
        expectedModel.enroll(BRYAN.getId(), MATHEMATICS.getId());
        expectedModel.markPaid(ALICE.getId());

        MarkCommand command = new MarkCommand(MATHEMATICS.getId());
        String expectedMessage = String.format(MarkCommand.MESSAGE_SUCCESS_WITH_ALREADY_PAID,
                ALICE.getName().fullName, BRYAN.getName().fullName);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_lessonAllAlreadyPaid_throwsCommandException() {
        MarkCommand command = new MarkCommand(SCIENCE_S2.getId());
        assertCommandFailure(command, model,
                String.format(MarkCommand.MESSAGE_LESSON_ALL_ALREADY_PAID, SCIENCE_S2.getId()));
    }

    @Test
    public void execute_lessonNoStudents_throwsCommandException() {
        Lesson emptyLesson = model.addLesson(HISTORY.getSubject(), HISTORY.getLevel(),
                HISTORY.getTimeslot(), HISTORY.getFee());
        MarkCommand command = new MarkCommand(emptyLesson.getId());
        assertCommandFailure(command, model,
                String.format(MarkCommand.MESSAGE_NO_STUDENTS_IN_LESSON, emptyLesson.getId()));
    }

    @Test
    public void execute_lessonNotFound_throwsCommandException() {
        LessonId missingLessonId = new LessonId(999);
        MarkCommand command = new MarkCommand(missingLessonId);
        assertCommandFailure(command, model,
                String.format(MESSAGE_LESSON_NOT_FOUND, missingLessonId));
    }

    @Test
    public void equals_and_hashCode() {
        MarkCommand markAlice = new MarkCommand(ALICE.getId());
        MarkCommand markAliceCopy = new MarkCommand(ALICE.getId());
        MarkCommand markBryan = new MarkCommand(BRYAN.getId());
        MarkCommand markMultiple = new MarkCommand(Arrays.asList(ALICE.getId(), BRYAN.getId()));
        MarkCommand markLesson = new MarkCommand(MATHEMATICS.getId());

        assertTrue(markAlice.equals(markAlice));
        assertTrue(markAlice.equals(markAliceCopy));
        assertEquals(markAlice.hashCode(), markAliceCopy.hashCode());

        assertFalse(markAlice.equals(1));
        assertFalse(markAlice.equals(null));
        assertFalse(markAlice.equals(markBryan));
        assertFalse(markAlice.equals(markMultiple));
        assertFalse(markAlice.equals(markLesson));
    }
}
