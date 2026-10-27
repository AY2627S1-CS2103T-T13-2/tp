package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudentId;

/** Parses arguments for {@link UnmarkCommand}. */
public class UnmarkCommandParser implements Parser<UnmarkCommand> {
    @Override
    public UnmarkCommand parse(String args) throws ParseException {
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 1 || parts[0].isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkCommand.MESSAGE_USAGE));
        }
        StudentId studentId = ParserUtil.parseStudentId(parts[0]);
        return new UnmarkCommand(studentId);
    }
}
