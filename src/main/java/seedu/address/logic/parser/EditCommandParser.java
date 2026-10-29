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
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.LessonEdit;
import seedu.address.logic.commands.EditCommand.StudentEdit;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.StudentId;

/** Parses input arguments and creates an {@link EditCommand}. */
public class EditCommandParser implements Parser<EditCommand> {

    private static final Prefix[] ALL_PREFIXES = {PREFIX_STUDENT, PREFIX_LESSON, PREFIX_CONTACT,
        PREFIX_STUDENT_LEVEL, PREFIX_LESSON_LEVEL, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME,
        PREFIX_FEE};
    private static final Prefix[] STUDENT_PREFIXES = {PREFIX_STUDENT, PREFIX_CONTACT, PREFIX_STUDENT_LEVEL};
    private static final Prefix[] LESSON_PREFIXES = {PREFIX_LESSON, PREFIX_LESSON_LEVEL, PREFIX_DAY,
        PREFIX_START_TIME, PREFIX_END_TIME, PREFIX_FEE};

    @FunctionalInterface
    private interface ArgumentValueParser<T> {
        T parse(String value) throws ParseException;
    }

    @Override
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, ALL_PREFIXES);
        arguments.verifyNoPrefixInsideValues(ALL_PREFIXES);
        ArgumentTokenizer.verifyNoUnknownPrefixes(args, EditCommand.MESSAGE_USAGE, ALL_PREFIXES);
        String[] target = arguments.getPreamble().trim().split("\\s+");
        if (target.length != 2 || !isKind(target[0]) || containsKnownGluedPrefix(target[1])) {
            throw createInvalidFormatException();
        }

        arguments.verifyNoDuplicatePrefixesFor(ALL_PREFIXES);
        if (!anyGiven(arguments, ALL_PREFIXES)) {
            throw new ParseException(EditCommand.MESSAGE_NO_FIELDS);
        }

        return target[0].equalsIgnoreCase("student")
                ? parseStudent(target[1], arguments)
                : parseLesson(target[1], arguments);
    }

    private static EditCommand parseStudent(String idValue, ArgumentMultimap arguments)
            throws ParseException {
        if (anyGiven(arguments, LESSON_PREFIXES)) {
            throw new ParseException(EditCommand.MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED);
        }
        StudentId id = ParserUtil.parseStudentId(idValue);
        Optional<Name> name = parseOptionalArgument(arguments, PREFIX_STUDENT, ParserUtil::parseName);
        Optional<Contact> contact = parseOptionalArgument(arguments, PREFIX_CONTACT,
                ParserUtil::parseContact);
        Optional<Level> level = parseOptionalArgument(arguments, PREFIX_STUDENT_LEVEL,
                ParserUtil::parseStudentLevel);
        return new EditCommand(new StudentEdit(id, name, contact, level));
    }

    private static EditCommand parseLesson(String idValue, ArgumentMultimap arguments) throws ParseException {
        if (anyGiven(arguments, STUDENT_PREFIXES)) {
            throw new ParseException(EditCommand.MESSAGE_LESSON_PARAMETERS_NOT_ALLOWED);
        }
        LessonId id = ParserUtil.parseLessonId(idValue);
        Optional<Subject> subject = parseOptionalArgument(arguments, PREFIX_LESSON, ParserUtil::parseSubject);
        Optional<Level> level = parseOptionalArgument(arguments, PREFIX_LESSON_LEVEL,
                ParserUtil::parseLessonLevel);
        Optional<DayOfWeek> day = parseOptionalArgument(arguments, PREFIX_DAY, ParserUtil::parseDay);
        Optional<LocalTime> start = parseOptionalArgument(arguments, PREFIX_START_TIME,
                ParserUtil::parseStartTime);
        Optional<LocalTime> end = parseOptionalArgument(arguments, PREFIX_END_TIME,
                EditCommandParser::parseEndTime);
        Optional<Fee> fee = parseOptionalArgument(arguments, PREFIX_FEE, ParserUtil::parseFee);
        return new EditCommand(new LessonEdit(id, subject, level, day, start, end, fee));
    }

    /** Parses one optional argument when it is present. */
    private static <T> Optional<T> parseOptionalArgument(ArgumentMultimap arguments, Prefix prefix,
            ArgumentValueParser<T> parser) throws ParseException {
        Optional<String> value = arguments.getValue(prefix);
        return value.isPresent() ? Optional.of(parser.parse(value.get())) : Optional.empty();
    }

    private static LocalTime parseEndTime(String value) throws ParseException {
        String trimmedValue = value.trim();
        if (trimmedValue.equals("24:00")) {
            throw new ParseException(Timeslot.MESSAGE_MIDNIGHT_END);
        }
        return Timeslot.parseTime(trimmedValue)
                .orElseThrow(() -> new ParseException(Timeslot.MESSAGE_INVALID_END_TIME));
    }

    private static boolean containsKnownGluedPrefix(String value) {
        return Stream.of(ALL_PREFIXES)
                .anyMatch(prefix -> ArgumentMultimap.containsGluedPrefix(value, prefix.getPrefix()));
    }

    private static boolean anyGiven(ArgumentMultimap arguments, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> arguments.getValue(prefix).isPresent());
    }

    private static boolean isKind(String value) {
        String kind = value.toLowerCase(Locale.ROOT);
        return kind.equals("student") || kind.equals("lesson");
    }

    private static ParseException createInvalidFormatException() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
    }
}
