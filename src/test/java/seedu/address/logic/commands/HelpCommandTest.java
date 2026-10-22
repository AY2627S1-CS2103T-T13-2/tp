package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.HelpCommand.SHOWING_HELP_MESSAGE;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class HelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_help_success() {
        CommandResult expectedCommandResult = new CommandResult(SHOWING_HELP_MESSAGE, true, false);
        assertCommandSuccess(new HelpCommand(), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void messageUsage_containsEveryAvailableCommandFormat() {
        assertTrue(HelpCommand.MESSAGE_USAGE.contains(AddCommand.MESSAGE_USAGE));
        assertTrue(HelpCommand.MESSAGE_USAGE.contains(EditCommand.MESSAGE_USAGE));
        assertTrue(HelpCommand.MESSAGE_USAGE.contains(DeleteCommand.MESSAGE_USAGE));
        assertTrue(HelpCommand.MESSAGE_USAGE.contains(ListCommand.MESSAGE_USAGE));
        assertTrue(HelpCommand.MESSAGE_USAGE.contains(MarkCommand.MESSAGE_USAGE));
        assertTrue(HelpCommand.MESSAGE_USAGE.contains(ScheduleCommand.MESSAGE_USAGE));
        assertTrue(HelpCommand.MESSAGE_USAGE.contains(FilterCommand.MESSAGE_USAGE));
        assertTrue(HelpCommand.MESSAGE_USAGE.contains(HelpCommand.COMMAND_WORD));
        assertTrue(HelpCommand.MESSAGE_USAGE.contains(ExitCommand.COMMAND_WORD));
    }
}
