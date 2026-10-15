package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FEE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_LEVEL;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddCommand.ExistingLesson;
import seedu.address.logic.commands.AddCommand.ExistingStudent;
import seedu.address.logic.commands.AddCommand.LessonRef;
import seedu.address.logic.commands.AddCommand.NewLesson;
import seedu.address.logic.commands.AddCommand.NewStudent;
import seedu.address.logic.commands.AddCommand.StudentRef;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;

/**
 * Parses input arguments and creates a new AddCommand object.
 *
 * <p>The value after {@code s/} is an existing student if it is an ID, and the name of a new student otherwise.
 * {@code l/} works the same way for lessons. Which of the other parameters are required, and which are not allowed,
 * follows from that. The checks run in this order, and the first problem found is the one reported:
 * <ol>
 *   <li>the command is well formed: no text before the first parameter, and at least one parameter</li>
 *   <li>no parameter is given more than once</li>
 *   <li>both {@code s/} and {@code l/} are given</li>
 *   <li>no parameter is given that does not belong to the form: student parameters first, then lesson parameters</li>
 *   <li>no required parameter is missing</li>
 *   <li>every value is valid: the student's values first ({@code s/}, {@code c/}, {@code slv/}), then the
 *       lesson's ({@code l/}, {@code llv/}, {@code d/}, {@code st/}, {@code et/}, {@code f/})</li>
 * </ol>
 */
public class AddCommandParser implements Parser<AddCommand> {

    public static final String MESSAGE_MISSING_PARAMETER_FORMAT = "Missing required parameter: %s.";
    public static final String MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED =
            "c/ and slv/ can only be used when adding a new student.";
    public static final String MESSAGE_LESSON_PARAMETERS_NOT_ALLOWED =
            "llv/, d/, st/, et/ and f/ can only be used when adding a new lesson.";

    private static final Prefix[] ALL_PREFIXES = {PREFIX_STUDENT, PREFIX_LESSON, PREFIX_CONTACT,
        PREFIX_STUDENT_LEVEL, PREFIX_LESSON_LEVEL, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME, PREFIX_FEE};

    /** The parameters that only a new student has. */
    private static final Prefix[] NEW_STUDENT_PREFIXES = {PREFIX_CONTACT, PREFIX_STUDENT_LEVEL};

    /** The parameters that only a new lesson has. */
    private static final Prefix[] NEW_LESSON_PREFIXES = {PREFIX_LESSON_LEVEL, PREFIX_DAY, PREFIX_START_TIME,
        PREFIX_END_TIME, PREFIX_FEE};

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, ALL_PREFIXES);

        boolean hasNoParameters = Stream.of(ALL_PREFIXES).noneMatch(prefix -> argMultimap.getValue(prefix).isPresent());
        if (!argMultimap.getPreamble().isEmpty() || hasNoParameters) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(ALL_PREFIXES);

        // Whether each side is new or existing decides everything else, so both must be given first.
        List<Prefix> missing = missingPrefixes(argMultimap, PREFIX_STUDENT, PREFIX_LESSON);
        if (!missing.isEmpty()) {
            throw missingParameter(missing);
        }
        String studentValue = argMultimap.getValue(PREFIX_STUDENT).get();
        String lessonValue = argMultimap.getValue(PREFIX_LESSON).get();
        boolean isNewStudent = !ParserUtil.isId(studentValue);
        boolean isNewLesson = !ParserUtil.isId(lessonValue);

        if (!isNewStudent && anyGiven(argMultimap, NEW_STUDENT_PREFIXES)) {
            throw new ParseException(MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED);
        }
        if (!isNewLesson && anyGiven(argMultimap, NEW_LESSON_PREFIXES)) {
            throw new ParseException(MESSAGE_LESSON_PARAMETERS_NOT_ALLOWED);
        }

        List<Prefix> allMissing = new ArrayList<>();
        if (isNewStudent) {
            allMissing.addAll(missingPrefixes(argMultimap, NEW_STUDENT_PREFIXES));
        }
        if (isNewLesson) {
            allMissing.addAll(missingPrefixes(argMultimap, NEW_LESSON_PREFIXES));
        }
        if (!allMissing.isEmpty()) {
            throw missingParameter(allMissing);
        }

        StudentRef student = isNewStudent ? parseNewStudent(argMultimap) : parseExistingStudent(studentValue);
        LessonRef lesson = isNewLesson ? parseNewLesson(argMultimap) : parseExistingLesson(lessonValue);
        return new AddCommand(student, lesson);
    }

    private static StudentRef parseExistingStudent(String value) throws ParseException {
        return new ExistingStudent(ParserUtil.parseStudentId(value));
    }

    private static StudentRef parseNewStudent(ArgumentMultimap argMultimap) throws ParseException {
        return new NewStudent(
                ParserUtil.parseName(argMultimap.getValue(PREFIX_STUDENT).get()),
                ParserUtil.parseContact(argMultimap.getValue(PREFIX_CONTACT).get()),
                ParserUtil.parseStudentLevel(argMultimap.getValue(PREFIX_STUDENT_LEVEL).get()));
    }

    private static LessonRef parseExistingLesson(String value) throws ParseException {
        return new ExistingLesson(ParserUtil.parseLessonId(value));
    }

    private static LessonRef parseNewLesson(ArgumentMultimap argMultimap) throws ParseException {
        Subject subject = ParserUtil.parseSubject(argMultimap.getValue(PREFIX_LESSON).get());
        Level level = ParserUtil.parseLessonLevel(argMultimap.getValue(PREFIX_LESSON_LEVEL).get());
        DayOfWeek day = ParserUtil.parseDay(argMultimap.getValue(PREFIX_DAY).get());
        LocalTime start = ParserUtil.parseStartTime(argMultimap.getValue(PREFIX_START_TIME).get());
        LocalTime end = ParserUtil.parseEndTime(argMultimap.getValue(PREFIX_END_TIME).get(), start);
        Fee fee = ParserUtil.parseFee(argMultimap.getValue(PREFIX_FEE).get());
        return new NewLesson(subject, level, new Timeslot(day, start, end), fee);
    }

    /**
     * Returns the prefixes among {@code prefixes} that were not given, in the order they were listed.
     */
    private static List<Prefix> missingPrefixes(ArgumentMultimap argMultimap, Prefix... prefixes) {
        return Stream.of(prefixes)
                .filter(prefix -> argMultimap.getValue(prefix).isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Returns true if any of {@code prefixes} was given.
     */
    private static boolean anyGiven(ArgumentMultimap argMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argMultimap.getValue(prefix).isPresent());
    }

    private static ParseException missingParameter(List<Prefix> missing) {
        String names = missing.stream().map(Prefix::toString).collect(Collectors.joining(", "));
        return new ParseException(String.format(MESSAGE_MISSING_PARAMETER_FORMAT, names));
    }

}
