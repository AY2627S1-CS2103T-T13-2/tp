package seedu.address.logic.commands;

import static seedu.address.logic.Messages.MESSAGE_NOT_IMPLEMENTED;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Deletes one student or one lesson by ID, together with its enrollments.
 *
 * <p>This is a stub: the command is registered in the parser, but its behaviour belongs to the owner of this
 * feature. See specification codes DEL-1 to DEL-6.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " student|lesson ID";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED);
    }
}
