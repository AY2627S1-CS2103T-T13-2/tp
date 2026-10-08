package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudentId;

/**
 * Parses input arguments and creates a new MarkCommand object.
 */
public class MarkCommandParser implements Parser<MarkCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the MarkCommand
     * and returns a MarkCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public MarkCommand parse(String args) throws ParseException {
        String[] arguments = args.trim().split("\\s+");
        if (arguments.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
        }

        StudentId studentId = ParserUtil.parseStudentId(arguments[0]);
        YearMonth month;
        try {
            month = YearMonth.parse(arguments[1]);
        } catch (DateTimeParseException exception) {
            throw new ParseException("Month must be in YYYY-MM format.", exception);
        }
        return new MarkCommand(studentId, month);
    }

}
