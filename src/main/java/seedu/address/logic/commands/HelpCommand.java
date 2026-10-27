package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Formats full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = String.join("\n",
            "Available commands:",
            AddCommand.MESSAGE_USAGE,
            EditCommand.MESSAGE_USAGE,
            DeleteCommand.MESSAGE_USAGE,
            ListCommand.MESSAGE_USAGE,
            MarkCommand.MESSAGE_USAGE,
            ScheduleCommand.MESSAGE_USAGE,
            FilterCommand.MESSAGE_USAGE,
            COMMAND_WORD,
            ExitCommand.COMMAND_WORD);

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(SHOWING_HELP_MESSAGE, true, false);
    }

    @Override
    public boolean isDataMutating() {
        return false;
    }
}
