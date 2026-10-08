package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

/** Parses input arguments and creates a {@link DeleteCommand}. */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    private static final String MESSAGE_USAGE = DeleteCommand.MESSAGE_USAGE;
    private static final String KIND_AND_ID_FORMAT = "(?i)(student|lesson)\\s+\\d+";

    /**
     * Parses the given arguments and returns a DeleteCommand.
     *
     * @throws ParseException if the input does not match the required format
     */
    public DeleteCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (!trimmedArgs.matches(KIND_AND_ID_FORMAT)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }

        String[] parts = trimmedArgs.split("\\s+");
        if (!ParserUtil.isId(parts[1])) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }
        if (parts[0].equalsIgnoreCase("student")) {
            StudentId id = ParserUtil.parseStudentId(parts[1]);
            return new DeleteCommand(id);
        }
        LessonId id = ParserUtil.parseLessonId(parts[1]);
        return new DeleteCommand(id);
    }
}
