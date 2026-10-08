package seedu.address.logic.commands;

import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
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
}
