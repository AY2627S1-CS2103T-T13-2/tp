package seedu.address.logic.commands;

import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.StudentId;

public class MarkCommandTest {

    private static final YearMonth FUTURE_MONTH = YearMonth.of(2026, 12);

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalTuiTracker(), new UserPrefs());
        expectedModel = new ModelManager(model.getTuiTracker(), new UserPrefs());
    }

    @Test
    public void execute_existingStudent_marksPaidThroughMonth() {
        expectedModel.markPaid(ALICE.getId(), FUTURE_MONTH);
        MarkCommand command = new MarkCommand(ALICE.getId(), FUTURE_MONTH);
        String expectedMessage = String.format(MarkCommand.MESSAGE_SUCCESS, ALICE.getName(), FUTURE_MONTH);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_missingStudent_throwsCommandException() {
        StudentId missingId = new StudentId(999);
        MarkCommand command = new MarkCommand(missingId, FUTURE_MONTH);

        assertCommandFailure(command, model, String.format(MESSAGE_STUDENT_NOT_FOUND, missingId));
    }

    @Test
    public void execute_sameOrEarlierMonth_throwsCommandException() {
        MarkCommand sameMonth = new MarkCommand(BRYAN.getId(), BRYAN.getLastPaidMonth().orElseThrow());
        String expectedMessage = String.format(MarkCommand.MESSAGE_ALREADY_PAID, BRYAN.getName(),
                BRYAN.getLastPaidMonth().orElseThrow());
        assertCommandFailure(sameMonth, model, expectedMessage);

        MarkCommand earlierMonth = new MarkCommand(BRYAN.getId(),
                BRYAN.getLastPaidMonth().orElseThrow().minusMonths(1));
        assertCommandFailure(earlierMonth, model, expectedMessage);
    }
}
