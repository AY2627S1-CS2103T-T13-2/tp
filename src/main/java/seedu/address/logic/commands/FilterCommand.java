package seedu.address.logic.commands;

import static seedu.address.logic.Messages.MESSAGE_NOT_IMPLEMENTED;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Shows only the lessons of one student, or only the students of one lesson.
 *
 * <p>This is a stub: the command is registered in the parser, but its behaviour belongs to the owner of this
 * feature. See specification codes FLT-1 to FLT-9 (a draft that is not yet agreed).
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " student|lesson ID";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED);
    }
}
