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
}
