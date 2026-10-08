package seedu.address.logic.commands;

import static seedu.address.logic.Messages.MESSAGE_NOT_IMPLEMENTED;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Enrolls a student in a lesson. The student and the lesson can each be new or existing.
 *
 * <p>This is a stub: the command is registered in the parser, but its behaviour belongs to the owner of this
 * feature. See specification codes ADD-1 to ADD-15.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " s/STUDENT l/LESSON [c/CONTACT slv/STUDENT_LEVEL] "
            + "[llv/LESSON_LEVEL d/DAY st/START_TIME et/END_TIME f/FEE]";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED);
    }
}
