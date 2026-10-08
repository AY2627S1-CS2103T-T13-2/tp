package seedu.address.logic.commands;

import static seedu.address.logic.Messages.MESSAGE_NOT_IMPLEMENTED;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Marks a student as paid for the current month.
 *
 * <p>This is a stub: the command is registered in the parser, but its behaviour belongs to the owner of this
 * feature. See specification codes MRK-1 to MRK-4.
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " ID";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED);
    }
}
