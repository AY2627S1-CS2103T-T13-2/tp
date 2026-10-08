package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model) throws CommandException;

    /**
     * Returns the message to show when the command succeeded but its changes could not be saved. The changes are
     * undone before this message is shown. A command whose specification asks for its own wording overrides this.
     *
     * @param defaultMessage The general message for a failed save.
     */
    public String getSaveFailureMessage(String defaultMessage) {
        return defaultMessage;
    }

}
