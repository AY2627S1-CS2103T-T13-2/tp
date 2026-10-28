package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses input arguments and creates a {@link DeleteCommand}. */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

    /**
     * Parses the given arguments and returns a DeleteCommand.
     *
     * @throws ParseException if the input does not match the required format
     */
    public DeleteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 2) {
            throw new ParseException(MESSAGE_INVALID_FORMAT);
        }

        String kind = parts[0];
        String id = parts[1];
        try {
            if (kind.equalsIgnoreCase("student")) {
                return new DeleteCommand(ParserUtil.parseStudentId(id));
            }
            if (kind.equalsIgnoreCase("lesson")) {
                return new DeleteCommand(ParserUtil.parseLessonId(id));
            }
        } catch (ParseException e) {
            if (ParserUtil.MESSAGE_ID_TOO_LARGE.equals(e.getMessage())) {
                throw e;
            }
            throw new ParseException(MESSAGE_INVALID_FORMAT, e);
        }
        throw new ParseException(MESSAGE_INVALID_FORMAT);
    }
}
