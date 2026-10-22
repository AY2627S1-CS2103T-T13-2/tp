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
        PREFIX_STUDENT_LEVEL, PREFIX_LESSON_LEVEL, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME, PREFIX_FEE};
    private static final Prefix[] STUDENT_PREFIXES = {PREFIX_STUDENT, PREFIX_CONTACT, PREFIX_STUDENT_LEVEL};
    private static final Prefix[] LESSON_PREFIXES = {PREFIX_LESSON, PREFIX_LESSON_LEVEL, PREFIX_DAY,
        PREFIX_START_TIME, PREFIX_END_TIME, PREFIX_FEE};

    @Override
    public EditCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, ALL_PREFIXES);
        String[] target = arguments.getPreamble().trim().split("\\s+");
        if (target.length != 2 || !isKind(target[0])) {
            throw invalidFormat();
        }

        arguments.verifyNoDuplicatePrefixesFor(ALL_PREFIXES);
        if (!anyGiven(arguments, ALL_PREFIXES)) {
            throw new ParseException(EditCommand.MESSAGE_NO_FIELDS);
        }

        return target[0].equalsIgnoreCase("student")
                ? parseStudent(target[1], arguments)
                : parseLesson(target[1], arguments);
    }

    private static EditCommand parseStudent(String idValue, ArgumentMultimap arguments) throws ParseException {
        if (anyGiven(arguments, LESSON_PREFIXES)) {
            throw new ParseException(EditCommand.MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED);
        }
        StudentId id = ParserUtil.parseStudentId(idValue);
        Optional<Name> name = parseName(arguments);
        Optional<Contact> contact = parseContact(arguments);
        Optional<Level> level = parseStudentLevel(arguments);
        return new EditCommand(new StudentEdit(id, name, contact, level));
    }

    private static EditCommand parseLesson(String idValue, ArgumentMultimap arguments) throws ParseException {
        if (anyGiven(arguments, STUDENT_PREFIXES)) {
            throw new ParseException(EditCommand.MESSAGE_LESSON_PARAMETERS_NOT_ALLOWED);
        }
        LessonId id = ParserUtil.parseLessonId(idValue);
        Optional<Subject> subject = parseSubject(arguments);
        Optional<Level> level = parseLessonLevel(arguments);
        Optional<DayOfWeek> day = parseDay(arguments);
        Optional<LocalTime> start = parseStart(arguments);
        Optional<LocalTime> end = parseEnd(arguments);
        Optional<Fee> fee = parseFee(arguments);
        return new EditCommand(new LessonEdit(id, subject, level, day, start, end, fee));
    }

    private static Optional<Name> parseName(ArgumentMultimap arguments) throws ParseException {
        return arguments.getValue(PREFIX_STUDENT).isPresent()
                ? Optional.of(ParserUtil.parseName(arguments.getValue(PREFIX_STUDENT).get()))
                : Optional.empty();
    }

    private static Optional<Contact> parseContact(ArgumentMultimap arguments) throws ParseException {
        return arguments.getValue(PREFIX_CONTACT).isPresent()
                ? Optional.of(ParserUtil.parseContact(arguments.getValue(PREFIX_CONTACT).get()))
                : Optional.empty();
    }

    private static Optional<Level> parseStudentLevel(ArgumentMultimap arguments) throws ParseException {
        return arguments.getValue(PREFIX_STUDENT_LEVEL).isPresent()
                ? Optional.of(ParserUtil.parseStudentLevel(arguments.getValue(PREFIX_STUDENT_LEVEL).get()))
                : Optional.empty();
    }

    private static Optional<Subject> parseSubject(ArgumentMultimap arguments) throws ParseException {
        return arguments.getValue(PREFIX_LESSON).isPresent()
                ? Optional.of(ParserUtil.parseSubject(arguments.getValue(PREFIX_LESSON).get()))
                : Optional.empty();
    }

    private static Optional<Level> parseLessonLevel(ArgumentMultimap arguments) throws ParseException {
        return arguments.getValue(PREFIX_LESSON_LEVEL).isPresent()
                ? Optional.of(ParserUtil.parseLessonLevel(arguments.getValue(PREFIX_LESSON_LEVEL).get()))
                : Optional.empty();
    }

    private static Optional<DayOfWeek> parseDay(ArgumentMultimap arguments) throws ParseException {
        return arguments.getValue(PREFIX_DAY).isPresent()
                ? Optional.of(ParserUtil.parseDay(arguments.getValue(PREFIX_DAY).get()))
                : Optional.empty();
    }

    private static Optional<LocalTime> parseStart(ArgumentMultimap arguments) throws ParseException {
        return arguments.getValue(PREFIX_START_TIME).isPresent()
                ? Optional.of(ParserUtil.parseStartTime(arguments.getValue(PREFIX_START_TIME).get()))
                : Optional.empty();
    }

    private static Optional<LocalTime> parseEnd(ArgumentMultimap arguments) throws ParseException {
        if (arguments.getValue(PREFIX_END_TIME).isEmpty()) {
            return Optional.empty();
        }
        String value = arguments.getValue(PREFIX_END_TIME).get().trim();
        return Optional.of(Timeslot.parseTime(value)
                .orElseThrow(() -> new ParseException(Timeslot.MESSAGE_INVALID_END_TIME)));
    }

    private static Optional<Fee> parseFee(ArgumentMultimap arguments) throws ParseException {
        return arguments.getValue(PREFIX_FEE).isPresent()
                ? Optional.of(ParserUtil.parseFee(arguments.getValue(PREFIX_FEE).get()))
                : Optional.empty();
    }

    private static boolean anyGiven(ArgumentMultimap arguments, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> arguments.getValue(prefix).isPresent());
    }

    private static boolean isKind(String value) {
        String kind = value.toLowerCase(Locale.ROOT);
        return kind.equals("student") || kind.equals("lesson");
    }

    private static ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
    }
}
