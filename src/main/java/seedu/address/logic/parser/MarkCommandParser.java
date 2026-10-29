package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.LessonId;
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
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
        }

        String[] parts = trimmedArgs.split("\\s+");
        if (parts[0].equalsIgnoreCase("lesson")) {
            if (parts.length != 2) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE));
            }
            LessonId lessonId = ParserUtil.parseLessonId(parts[1]);
            return new MarkCommand(lessonId);
        }

        List<StudentId> studentIds = new ArrayList<>();
        for (String part : parts) {
            studentIds.add(ParserUtil.parseStudentId(part));
        }
        return new MarkCommand(studentIds);
    }
}
