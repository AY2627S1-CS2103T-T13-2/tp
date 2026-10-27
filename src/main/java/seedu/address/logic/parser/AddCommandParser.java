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

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public static final String MESSAGE_PREFIX_INSIDE_VALUE =
            "%1$s was found inside the value of %2$s. Separate parameters with a space.";
    public static final String MESSAGE_NOT_AN_ID =
            "%1$s%2$s is neither an ID nor a valid %3$s. An ID is a positive whole number, such as %1$s3.";
    /** Added to a missing-parameter message, to say which value was read as a new student or lesson. */
    public static final String MESSAGE_READ_AS_NEW = " %1$s%2$s was read as a new %3$s.";
    /** Added after {@link #MESSAGE_READ_AS_NEW} when the value starts with an ID followed by more text. */
    public static final String MESSAGE_USE_ID_ONLY = " To use an existing %1$s, give only its ID, such as %2$s%3$s.";
    public static final String MESSAGE_ID_TOO_LARGE = ParserUtil.MESSAGE_ID_TOO_LARGE;

    private static final Logger logger = LogsCenter.getLogger(AddCommandParser.class);

    /** A number that is not a valid ID, such as {@code 0}, {@code -1}, {@code 1.5} or {@code #1}. */
    private static final Pattern LOOKS_LIKE_A_NUMBER = Pattern.compile("^[#+-]?\\d+([.,]\\d+)?$");

    /** The largest ID a student or lesson can have. */
    private static final BigInteger MAX_ID = BigInteger.valueOf(Integer.MAX_VALUE);

    /** An ID followed by a space and more text, such as {@code 2 extra words}. */
    private static final Pattern ID_THEN_TEXT = Pattern.compile("^(\\d+)\\s+\\S.*$");

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
            // Nothing about the input is logged: it holds names and contact numbers, and so can the error message,
            // which repeats what was typed, such as "s/Daniel Koh was read as a new student".
            logger.fine("Rejected add command because its arguments are invalid");
            throw e;
        }
    }

    private AddCommand parseArguments(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, ALL_PREFIXES);
        checkWellFormed(argMultimap);
        argMultimap.verifyNoDuplicatePrefixesFor(ALL_PREFIXES);
        checkNoPrefixInsideValues(argMultimap);
        checkStudentAndLessonGiven(argMultimap);

        String studentValue = requiredValue(argMultimap, PREFIX_STUDENT);
        String lessonValue = requiredValue(argMultimap, PREFIX_LESSON);
        checkNumberIsAnId(PREFIX_STUDENT, studentValue, "name");
        checkNumberIsAnId(PREFIX_LESSON, lessonValue, "subject");
        checkIdNotTooLarge(studentValue);
        checkIdNotTooLarge(lessonValue);
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
     * Throws if a parameter appears inside another parameter's value without a space before it, such as the
     * {@code slv/} in {@code c/92223333slv/Primary 6}. Such a parameter would otherwise be reported as missing.
     */
    private static void checkNoPrefixInsideValues(ArgumentMultimap argMultimap) throws ParseException {
        for (Prefix given : ALL_PREFIXES) {
            Optional<String> value = argMultimap.getValue(given);
            if (value.isEmpty()) {
                continue;
            }
            for (Prefix inside : ALL_PREFIXES) {
                if (containsGluedPrefix(value.get(), inside.getPrefix())) {
                    throw new ParseException(String.format(MESSAGE_PREFIX_INSIDE_VALUE, inside, given));
                }
            }
        }
    }

    /**
     * Returns true if {@code prefix} appears in {@code value}, ignoring letter case, somewhere other than right after
     * whitespace.
     */
    private static boolean containsGluedPrefix(String value, String prefix) {
        for (int i = 1; i + prefix.length() <= value.length(); i++) {
            if (value.regionMatches(true, i, prefix, 0, prefix.length())
                    && !Character.isWhitespace(value.charAt(i - 1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Throws if {@code value} looks like a number but is not a valid ID, such as {@code 0} or {@code 1.5}. Without
     * this check it would be read as the name of a new student or the subject of a new lesson.
     */
    private static void checkNumberIsAnId(Prefix prefix, String value, String kindOfName) throws ParseException {
        if (LOOKS_LIKE_A_NUMBER.matcher(value).matches() && !ParserUtil.isId(value)) {
            throw new ParseException(String.format(MESSAGE_NOT_AN_ID, prefix, value, kindOfName));
        }
    }

    /**
     * Throws if {@code value} is an ID that is too large for any student or lesson to have.
     */
    private static void checkIdNotTooLarge(String value) throws ParseException {
        if (ParserUtil.isId(value) && new BigInteger(value.trim()).compareTo(MAX_ID) > 0) {
            throw new ParseException(MESSAGE_ID_TOO_LARGE);
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
            String readAsNew = (isNewStudent && anyMissing(missing, NEW_STUDENT_PREFIXES)
                    ? readAsNew(PREFIX_STUDENT, requiredValue(argMultimap, PREFIX_STUDENT), "student") : "")
                    + (isNewLesson && anyMissing(missing, NEW_LESSON_PREFIXES)
                    ? readAsNew(PREFIX_LESSON, requiredValue(argMultimap, PREFIX_LESSON), "lesson") : "");
            throw new ParseException(missingParameter(missing).getMessage() + readAsNew);
        }
    }

    private static boolean anyMissing(List<Prefix> missing, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(missing::contains);
    }

    /**
     * Returns the sentence that says {@code value} was read as a new student or lesson, with a hint to give only
     * the ID when the value starts with an ID followed by more text.
     */
    private static String readAsNew(Prefix prefix, String value, String kind) {
        String sentence = String.format(MESSAGE_READ_AS_NEW, prefix, value, kind);
        Matcher idThenText = ID_THEN_TEXT.matcher(value);
        if (idThenText.matches()) {
            sentence += String.format(MESSAGE_USE_ID_ONLY, kind, prefix, idThenText.group(1));
        }
        return sentence;
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
