package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

/**
 * Parses input arguments and creates a new FilterCommand object.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    private static final String KEYWORD_STUDENT = "student";
    private static final String KEYWORD_LESSON = "lesson";

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns a FilterCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] parts = args.trim().split("\\s+");

        if (parts.length != 2 || parts[0].isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        String keyword = parts[0];
        String id = parts[1];

        if (keyword.equals(KEYWORD_STUDENT)) {
            StudentId studentId = ParserUtil.parseStudentId(id);
            return new FilterCommand(studentId);
        }

        if (keyword.equals(KEYWORD_LESSON)) {
            LessonId lessonId = ParserUtil.parseLessonId(id);
            return new FilterCommand(lessonId);
        }

        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

}
