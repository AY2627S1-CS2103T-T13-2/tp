package seedu.address.logic.commands;

import static seedu.address.logic.Messages.MESSAGE_NOT_IMPLEMENTED;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Shows all students or all lessons, and clears any active filter.
 *
 * <p>This is a stub: the command is registered in the parser, but its behaviour belongs to the owner of this
 * feature. See specification codes LST-1 to LST-9.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " [students|lessons]";

    public static final String MESSAGE_INVALID_VIEW = "Invalid view. Use: " + MESSAGE_USAGE;

    public static final String MESSAGE_VIEW_GIVEN_TWICE = "The view can only be specified once.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED);
    }
}
