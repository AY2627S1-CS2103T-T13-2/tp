package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.ScheduleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudentId;

/** Parses the {@code schedule} command and its optional student ID. */
public class ScheduleCommandParser implements Parser<ScheduleCommand> {

    @Override
    public ScheduleCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmed = args.trim();
        if (trimmed.isEmpty()) {
            return new ScheduleCommand();
        }

        String[] parts = trimmed.split("\\s+");
        if (!parts[0].equalsIgnoreCase("student")) {
            throw new ParseException(ScheduleCommand.MESSAGE_UNKNOWN_PARAMETER);
        }
        if (parts.length != 2) {
            throw new ParseException(ScheduleCommand.MESSAGE_INVALID_FORMAT);
        }

        StudentId studentId = ParserUtil.parseStudentId(parts[1]);
        return new ScheduleCommand(studentId);
    }
}
