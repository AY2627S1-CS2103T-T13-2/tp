package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
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
 * the bare keyword {@code paid} or {@code unpaid}, or one or both of {@code l/SUBJECT} and
 * {@code llv/LESSON_LEVEL}.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    public static final String MESSAGE_INVALID_SUBJECT = Subject.MESSAGE_CONSTRAINTS;
    public static final String MESSAGE_FILTER_EXAMPLES = "Choose a filter form. Examples: filter student 1, "
            + "filter lesson 2, filter day Monday, filter paid, or filter l/Mathematics llv/Primary 6.";
    public static final String MESSAGE_MISSING_FILTER_VALUE = "The %1$s filter needs a value. "
            + "Example: filter %1$s %2$s.";
    public static final String MESSAGE_UNKNOWN_FILTER = "Unknown filter type '%1$s'. Use student, lesson, day, "
            + "paid, unpaid, l/SUBJECT, or llv/LESSON_LEVEL. Example: filter student 1.";
    public static final String MESSAGE_EXTRA_FILTER_ARGUMENTS = "Too many values for the %1$s filter. "
            + "Example: filter %1$s %2$s.";
    public static final String MESSAGE_MIXED_FILTER_FORMS = "Do not combine a student, lesson, or day filter with "
            + "l/ or llv/. Example: filter l/Mathematics llv/Primary 6.";

    private static final String KEYWORD_STUDENT = "student";
    private static final String KEYWORD_LESSON = "lesson";
    private static final String KEYWORD_DAY = "day";
    private static final String KEYWORD_PAID = "paid";
    private static final String KEYWORD_UNPAID = "unpaid";

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

        String trimmedArgs = args.trim();
        if (trimmedArgs.equals(KEYWORD_PAID)) {
            return new FilterCommand(true);
        }
        if (trimmedArgs.equals(KEYWORD_UNPAID)) {
            return new FilterCommand(false);
        }

        String[] parts = args.trim().split("\\s+");

        if (parts.length == 1 && parts[0].isEmpty()) {
            throw new ParseException(MESSAGE_FILTER_EXAMPLES);
        }
        String keyword = parts[0];
        if (parts.length == 1 && isKeyword(keyword)) {
            throw new ParseException(String.format(MESSAGE_MISSING_FILTER_VALUE, keyword, exampleValue(keyword)));
        }
        if (parts.length > 2 && isKeyword(keyword)) {
            throw new ParseException(String.format(MESSAGE_EXTRA_FILTER_ARGUMENTS, keyword, exampleValue(keyword)));
        }
        if (!isKeyword(keyword)) {
            throw new ParseException(String.format(MESSAGE_UNKNOWN_FILTER, keyword));
        }

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

        throw new ParseException(String.format(MESSAGE_UNKNOWN_FILTER, keyword));
    }

    private static FilterCommand parseSubjectAndLevel(ArgumentMultimap argMultimap) throws ParseException {
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(MESSAGE_MIXED_FILTER_FORMS);
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

    private static boolean isKeyword(String keyword) {
        return keyword.equals(KEYWORD_STUDENT) || keyword.equals(KEYWORD_LESSON) || keyword.equals(KEYWORD_DAY);
    }

    private static String exampleValue(String keyword) {
        return keyword.equals(KEYWORD_DAY) ? "Monday" : keyword.equals(KEYWORD_STUDENT) ? "1" : "2";
    }

}
