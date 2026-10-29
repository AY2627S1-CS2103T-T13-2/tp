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

public class UnmarkCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), TypicalClock.CLOCK);
        expectedModel = new ModelManager(model.getTuiTracker(), new UserPrefs(), TypicalClock.CLOCK);
    }

    @Test
    public void execute_paidStudent_marksUnpaid() {
        expectedModel.unmarkPaid(BRYAN.getId());
        UnmarkCommand command = new UnmarkCommand(BRYAN.getId());
        String expectedMessage = String.format(UnmarkCommand.MESSAGE_SUCCESS, BRYAN.getName());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_missingStudent_throwsCommandException() {
        StudentId missingId = new StudentId(999);
        assertCommandFailure(new UnmarkCommand(missingId), model,
                String.format(MESSAGE_STUDENT_NOT_FOUND, missingId));
    }

    @Test
    public void execute_unpaidStudent_throwsCommandException() {
        assertCommandFailure(new UnmarkCommand(ALICE.getId()), model,
                String.format(UnmarkCommand.MESSAGE_ALREADY_UNPAID, ALICE.getName()));
    }

    @Test
    public void execute_multiplePaidStudents_marksAllUnpaid() {
        model.markPaid(ALICE.getId());
        expectedModel.markPaid(ALICE.getId());
        expectedModel.unmarkPaid(ALICE.getId());
        expectedModel.unmarkPaid(BRYAN.getId());

        UnmarkCommand command = new UnmarkCommand(Arrays.asList(ALICE.getId(), BRYAN.getId()));
        String expectedMessage = String.format(UnmarkCommand.MESSAGE_SUCCESS,
                ALICE.getName().fullName + ", " + BRYAN.getName().fullName);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleStudentsWithSomeAlreadyUnpaid_marksPaidAndNotesAlreadyUnpaid() {
        expectedModel.unmarkPaid(BRYAN.getId());
        UnmarkCommand command = new UnmarkCommand(Arrays.asList(ALICE.getId(), BRYAN.getId()));
        String expectedMessage = String.format(UnmarkCommand.MESSAGE_SUCCESS_WITH_ALREADY_UNPAID,
                BRYAN.getName().fullName, ALICE.getName().fullName);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleStudentsAllAlreadyUnpaid_throwsCommandException() {
        UnmarkCommand command = new UnmarkCommand(Arrays.asList(ALICE.getId(), CHLOE.getId()));
        assertCommandFailure(command, model, UnmarkCommand.MESSAGE_ALL_ALREADY_UNPAID);
    }

    @Test
    public void execute_multipleStudentsOneMissing_throwsCommandExceptionWithoutMutating() {
        StudentId missingId = new StudentId(999);
        UnmarkCommand command = new UnmarkCommand(Arrays.asList(BRYAN.getId(), missingId));

        assertCommandFailure(command, model, String.format(MESSAGE_STUDENT_NOT_FOUND, missingId));
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_lessonPaid_marksEnrolledStudentsUnpaid() {
        expectedModel.unmarkPaid(BRYAN.getId());
        UnmarkCommand command = new UnmarkCommand(SCIENCE_S2.getId());
        String expectedMessage = String.format(UnmarkCommand.MESSAGE_SUCCESS, BRYAN.getName().fullName);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_lessonSomeAlreadyUnpaid_marksPaidAndNotesAlreadyUnpaid() {
        model.enroll(BRYAN.getId(), MATHEMATICS.getId());
        expectedModel.enroll(BRYAN.getId(), MATHEMATICS.getId());
        expectedModel.unmarkPaid(BRYAN.getId());

        UnmarkCommand command = new UnmarkCommand(MATHEMATICS.getId());
        String expectedMessage = String.format(UnmarkCommand.MESSAGE_SUCCESS_WITH_ALREADY_UNPAID,
                BRYAN.getName().fullName, ALICE.getName().fullName);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_lessonAllAlreadyUnpaid_throwsCommandException() {
        UnmarkCommand command = new UnmarkCommand(MATHEMATICS.getId());
        assertCommandFailure(command, model,
                String.format(UnmarkCommand.MESSAGE_LESSON_ALL_ALREADY_UNPAID, MATHEMATICS.getId()));
    }

    @Test
    public void execute_lessonNoStudents_throwsCommandException() {
        Lesson emptyLesson = model.addLesson(HISTORY.getSubject(), HISTORY.getLevel(),
                HISTORY.getTimeslot(), HISTORY.getFee());
        UnmarkCommand command = new UnmarkCommand(emptyLesson.getId());
        assertCommandFailure(command, model,
                String.format(UnmarkCommand.MESSAGE_NO_STUDENTS_IN_LESSON, emptyLesson.getId()));
    }

    @Test
    public void execute_lessonNotFound_throwsCommandException() {
        LessonId missingLessonId = new LessonId(999);
        UnmarkCommand command = new UnmarkCommand(missingLessonId);
        assertCommandFailure(command, model,
                String.format(MESSAGE_LESSON_NOT_FOUND, missingLessonId));
    }

    @Test
    public void equals_and_hashCode() {
        UnmarkCommand unmarkBryan = new UnmarkCommand(BRYAN.getId());
        UnmarkCommand unmarkBryanCopy = new UnmarkCommand(BRYAN.getId());
        UnmarkCommand unmarkAlice = new UnmarkCommand(ALICE.getId());
        UnmarkCommand unmarkMultiple = new UnmarkCommand(Arrays.asList(ALICE.getId(), BRYAN.getId()));
        UnmarkCommand unmarkLesson = new UnmarkCommand(SCIENCE_S2.getId());

        assertTrue(unmarkBryan.equals(unmarkBryan));
        assertTrue(unmarkBryan.equals(unmarkBryanCopy));
        assertEquals(unmarkBryan.hashCode(), unmarkBryanCopy.hashCode());

        assertFalse(unmarkBryan.equals(1));
        assertFalse(unmarkBryan.equals(null));
        assertFalse(unmarkBryan.equals(unmarkAlice));
        assertFalse(unmarkBryan.equals(unmarkMultiple));
        assertFalse(unmarkBryan.equals(unmarkLesson));
    }
}
