package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

/** Parses arguments for {@link UnmarkCommand}. */
public class UnmarkCommandParser implements Parser<UnmarkCommand> {
    @Override
    public UnmarkCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkCommand.MESSAGE_USAGE));
        }

        String[] parts = trimmedArgs.split("\\s+");
        if (parts[0].equalsIgnoreCase("lesson")) {
            if (parts.length != 2) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkCommand.MESSAGE_USAGE));
            }
            LessonId lessonId = ParserUtil.parseLessonId(parts[1]);
            return new UnmarkCommand(lessonId);
        }

        List<StudentId> studentIds = new ArrayList<>();
        for (String part : parts) {
            studentIds.add(ParserUtil.parseStudentId(part));
        }
        return new UnmarkCommand(studentIds);
    }
}
