package seedu.address.logic;

import seedu.address.logic.parser.Prefix;

/**
 * Container for the user visible messages that more than one command uses.
 * A message that belongs to one command lives in that command's class instead.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";

    /** The usage line of a command is filled in, e.g. {@code delete student|lesson ID}. */
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format. Use: %1$s";

    public static final String MESSAGE_STUDENT_NOT_FOUND = "No student found with ID %1$s.";
    public static final String MESSAGE_LESSON_NOT_FOUND = "No lesson found with ID %1$s.";

    public static final String MESSAGE_DUPLICATE_PARAMETER = "A parameter can only be specified once.";

    public static final String MESSAGE_NOT_IMPLEMENTED = "This command is not implemented yet.";

    /**
     * Returns an error message indicating that a parameter was given more than once.
     * The prefixes are not named, because the specification asks for one fixed message.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        return MESSAGE_DUPLICATE_PARAMETER;
    }

}
