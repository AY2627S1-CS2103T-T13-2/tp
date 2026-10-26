package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
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
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.core.LogsCenter;
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

    private static final Logger logger = LogsCenter.getLogger(AddCommandParser.class);

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
        requireNonNull(args);
        try {
            return parseArguments(args);
        } catch (ParseException e) {
            // Only the error message is logged, never the input, because the input holds names and contact numbers.
            logger.fine("Rejected add command: " + e.getMessage());
            throw e;
        }
    }

    private AddCommand parseArguments(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, ALL_PREFIXES);
        checkWellFormed(argMultimap);
        argMultimap.verifyNoDuplicatePrefixesFor(ALL_PREFIXES);
        checkStudentAndLessonGiven(argMultimap);

        String studentValue = requiredValue(argMultimap, PREFIX_STUDENT);
        String lessonValue = requiredValue(argMultimap, PREFIX_LESSON);
        boolean isNewStudent = !ParserUtil.isId(studentValue);
        boolean isNewLesson = !ParserUtil.isId(lessonValue);
        logger.fine("Parsing add command: " + (isNewStudent ? "new" : "existing") + " student, "
                + (isNewLesson ? "new" : "existing") + " lesson");

        checkNoUnexpectedParameters(argMultimap, isNewStudent, isNewLesson);
        checkNoMissingParameters(argMultimap, isNewStudent, isNewLesson);

        StudentRef student = isNewStudent ? parseNewStudent(argMultimap) : parseExistingStudent(studentValue);
        LessonRef lesson = isNewLesson ? parseNewLesson(argMultimap) : parseExistingLesson(lessonValue);
        return new AddCommand(student, lesson);
    }

    /**
     * Throws if there is text before the first parameter, or if there is no parameter at all.
     */
    private static void checkWellFormed(ArgumentMultimap argMultimap) throws ParseException {
        if (!argMultimap.getPreamble().isEmpty() || !anyGiven(argMultimap, ALL_PREFIXES)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Throws if {@code s/} or {@code l/} is missing. Whether each side is new or existing decides everything else,
     * so both must be given before anything else is checked.
     */
    private static void checkStudentAndLessonGiven(ArgumentMultimap argMultimap) throws ParseException {
        List<Prefix> missing = missingPrefixes(argMultimap, PREFIX_STUDENT, PREFIX_LESSON);
        if (!missing.isEmpty()) {
            throw missingParameter(missing);
        }
    }

    /**
     * Throws if a parameter is given that only a new student or a new lesson has, for an existing one.
     */
    private static void checkNoUnexpectedParameters(ArgumentMultimap argMultimap, boolean isNewStudent,
            boolean isNewLesson) throws ParseException {
        if (!isNewStudent && anyGiven(argMultimap, NEW_STUDENT_PREFIXES)) {
            throw new ParseException(MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED);
        }
        if (!isNewLesson && anyGiven(argMultimap, NEW_LESSON_PREFIXES)) {
            throw new ParseException(MESSAGE_LESSON_PARAMETERS_NOT_ALLOWED);
        }
    }

    /**
     * Throws if a parameter that a new student or a new lesson needs is missing.
     */
    private static void checkNoMissingParameters(ArgumentMultimap argMultimap, boolean isNewStudent,
            boolean isNewLesson) throws ParseException {
        List<Prefix> missing = new ArrayList<>();
        if (isNewStudent) {
            missing.addAll(missingPrefixes(argMultimap, NEW_STUDENT_PREFIXES));
        }
        if (isNewLesson) {
            missing.addAll(missingPrefixes(argMultimap, NEW_LESSON_PREFIXES));
        }
        if (!missing.isEmpty()) {
            throw missingParameter(missing);
        }
    }

    private static StudentRef parseExistingStudent(String value) throws ParseException {
        return new ExistingStudent(ParserUtil.parseStudentId(value));
    }

    private static StudentRef parseNewStudent(ArgumentMultimap argMultimap) throws ParseException {
        return new NewStudent(
                ParserUtil.parseName(requiredValue(argMultimap, PREFIX_STUDENT)),
                ParserUtil.parseContact(requiredValue(argMultimap, PREFIX_CONTACT)),
                ParserUtil.parseStudentLevel(requiredValue(argMultimap, PREFIX_STUDENT_LEVEL)));
    }

    private static LessonRef parseExistingLesson(String value) throws ParseException {
        return new ExistingLesson(ParserUtil.parseLessonId(value));
    }

    private static LessonRef parseNewLesson(ArgumentMultimap argMultimap) throws ParseException {
        Subject subject = ParserUtil.parseSubject(requiredValue(argMultimap, PREFIX_LESSON));
        Level level = ParserUtil.parseLessonLevel(requiredValue(argMultimap, PREFIX_LESSON_LEVEL));
        DayOfWeek day = ParserUtil.parseDay(requiredValue(argMultimap, PREFIX_DAY));
        LocalTime start = ParserUtil.parseStartTime(requiredValue(argMultimap, PREFIX_START_TIME));
        LocalTime end = ParserUtil.parseEndTime(requiredValue(argMultimap, PREFIX_END_TIME), start);
        assert Timeslot.isValidTimeslot(start, end) : "parseEndTime must reject an end time that is not after start";
        Fee fee = ParserUtil.parseFee(requiredValue(argMultimap, PREFIX_FEE));
        return new NewLesson(subject, level, new Timeslot(day, start, end), fee);
    }

    /**
     * Returns the value of {@code prefix}, which must have been given.
     * The missing-parameter checks in {@link #parseArguments(String)} run first, so a missing value here is a bug
     * in this class and not a mistake by the user.
     */
    private static String requiredValue(ArgumentMultimap argMultimap, Prefix prefix) {
        Optional<String> value = argMultimap.getValue(prefix);
        assert value.isPresent() : "Required parameter " + prefix + " should have been checked before it is read";
        return value.get();
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
