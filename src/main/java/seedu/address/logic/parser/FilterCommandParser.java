package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON_LEVEL;

import java.time.DayOfWeek;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.student.StudentId;

/**
 * Parses input arguments and creates a new FilterCommand object.
 *
 * <p>The arguments are either a keyword and a value ({@code student ID}, {@code lesson ID} or {@code day DAY}),
 * or one or both of {@code l/SUBJECT} and {@code llv/LESSON_LEVEL}.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    public static final String MESSAGE_INVALID_SUBJECT = "Invalid subject. Subject must contain at least one letter, "
            + "may contain only letters, numbers, spaces and hyphens, and cannot exceed " + Subject.MAX_LENGTH
            + " characters.";

    private static final String KEYWORD_STUDENT = "student";
    private static final String KEYWORD_LESSON = "lesson";
    private static final String KEYWORD_DAY = "day";

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns a FilterCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);

        // A leading space lets a prefix at the very start of the arguments be recognised.
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + args, PREFIX_LESSON, PREFIX_LESSON_LEVEL);
        if (argMultimap.getValue(PREFIX_LESSON).isPresent() || argMultimap.getValue(PREFIX_LESSON_LEVEL).isPresent()) {
            return parseSubjectAndLevel(argMultimap);
        }

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

        if (keyword.equals(KEYWORD_DAY)) {
            DayOfWeek day = ParserUtil.parseDay(id);
            return new FilterCommand(day);
        }

        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    private static FilterCommand parseSubjectAndLevel(ArgumentMultimap argMultimap) throws ParseException {
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_LESSON, PREFIX_LESSON_LEVEL);

        Subject subject = null;
        if (argMultimap.getValue(PREFIX_LESSON).isPresent()) {
            String value = argMultimap.getValue(PREFIX_LESSON).get();
            if (!Subject.isValidSubject(value)) {
                throw new ParseException(MESSAGE_INVALID_SUBJECT);
            }
            subject = new Subject(value);
        }

        Level level = null;
        if (argMultimap.getValue(PREFIX_LESSON_LEVEL).isPresent()) {
            level = ParserUtil.parseLessonLevel(argMultimap.getValue(PREFIX_LESSON_LEVEL).get());
        }

        return new FilterCommand(subject, level);
    }

}
