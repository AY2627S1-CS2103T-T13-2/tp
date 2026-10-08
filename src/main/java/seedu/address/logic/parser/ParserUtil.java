package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalTime;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.StudentId;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 * Every method trims leading and trailing whitespace, and throws a {@code ParseException} carrying the exact
 * error message from the specification when the value is invalid.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_ID = "ID must be a positive whole number.";

    private static final String DIGITS_ONLY = "\\d+";

    /**
     * Returns true if {@code test} is an ID: digits only, no sign or decimal point, and not zero.
     * Leading zeros are allowed, so "007" is an ID.
     */
    public static boolean isId(String test) {
        requireNonNull(test);
        String trimmed = test.trim();
        return trimmed.matches(DIGITS_ONLY) && new BigInteger(trimmed).signum() > 0;
    }

    /**
     * Parses {@code id} into a {@code StudentId}. Leading zeros are ignored. A number too large to be an ID is
     * returned as the largest possible ID, which no student has.
     *
     * @throws ParseException if {@code id} is not a positive whole number.
     */
    public static StudentId parseStudentId(String id) throws ParseException {
        return new StudentId(parseIdNumber(id));
    }

    /**
     * Parses {@code id} into a {@code LessonId}. Leading zeros are ignored. A number too large to be an ID is
     * returned as the largest possible ID, which no lesson has.
     *
     * @throws ParseException if {@code id} is not a positive whole number.
     */
    public static LessonId parseLessonId(String id) throws ParseException {
        return new LessonId(parseIdNumber(id));
    }

    private static int parseIdNumber(String id) throws ParseException {
        requireNonNull(id);
        if (!isId(id)) {
            throw new ParseException(MESSAGE_INVALID_ID);
        }
        BigInteger number = new BigInteger(id.trim());
        return number.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 ? Integer.MAX_VALUE : number.intValue();
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String contact} into a {@code Contact}.
     *
     * @throws ParseException if the given {@code contact} is invalid.
     */
    public static Contact parseContact(String contact) throws ParseException {
        requireNonNull(contact);
        String trimmedContact = contact.trim();
        if (!Contact.isValidContact(trimmedContact)) {
            throw new ParseException(Contact.MESSAGE_CONSTRAINTS);
        }
        return new Contact(trimmedContact);
    }

    /**
     * Parses a {@code String level} into the {@code Level} of a student.
     *
     * @throws ParseException if the given {@code level} is invalid.
     */
    public static Level parseStudentLevel(String level) throws ParseException {
        requireNonNull(level);
        if (!Level.isValidLevel(level)) {
            throw new ParseException(Level.MESSAGE_CONSTRAINTS_STUDENT);
        }
        return new Level(level);
    }

    /**
     * Parses a {@code String level} into the {@code Level} of a lesson.
     *
     * @throws ParseException if the given {@code level} is invalid.
     */
    public static Level parseLessonLevel(String level) throws ParseException {
        requireNonNull(level);
        if (!Level.isValidLevel(level)) {
            throw new ParseException(Level.MESSAGE_CONSTRAINTS_LESSON);
        }
        return new Level(level);
    }

    /**
     * Parses a {@code String subject} into a {@code Subject}.
     *
     * @throws ParseException if the given {@code subject} is invalid.
     */
    public static Subject parseSubject(String subject) throws ParseException {
        requireNonNull(subject);
        String trimmedSubject = subject.trim();
        if (!Subject.isValidSubject(trimmedSubject)) {
            throw new ParseException(Subject.MESSAGE_CONSTRAINTS);
        }
        return new Subject(trimmedSubject);
    }

    /**
     * Parses a {@code String day} such as "monday" into a {@code DayOfWeek}. Letter case is ignored.
     *
     * @throws ParseException if the given {@code day} is not a day of the week.
     */
    public static DayOfWeek parseDay(String day) throws ParseException {
        requireNonNull(day);
        return Timeslot.parseDay(day).orElseThrow(() -> new ParseException(Timeslot.MESSAGE_INVALID_DAY));
    }

    /**
     * Parses a {@code String time} written exactly as {@code HH:mm} into the start time of a lesson.
     *
     * @throws ParseException if the given {@code time} is not in that format.
     */
    public static LocalTime parseStartTime(String time) throws ParseException {
        requireNonNull(time);
        return Timeslot.parseTime(time).orElseThrow(() -> new ParseException(Timeslot.MESSAGE_INVALID_START_TIME));
    }

    /**
     * Parses a {@code String time} written exactly as {@code HH:mm} into the end time of a lesson.
     *
     * @param start The start time of the same lesson, which the end time must be later than.
     * @throws ParseException if the given {@code time} is not in that format or is not later than {@code start}.
     */
    public static LocalTime parseEndTime(String time, LocalTime start) throws ParseException {
        requireNonNull(time);
        requireNonNull(start);
        LocalTime end = Timeslot.parseTime(time)
                .orElseThrow(() -> new ParseException(Timeslot.MESSAGE_INVALID_END_TIME));
        if (!Timeslot.isValidTimeslot(start, end)) {
            throw new ParseException(Timeslot.MESSAGE_INVALID_END_TIME);
        }
        return end;
    }

    /**
     * Parses a {@code String fee} into a {@code Fee}.
     *
     * @throws ParseException if the given {@code fee} is invalid.
     */
    public static Fee parseFee(String fee) throws ParseException {
        requireNonNull(fee);
        String trimmedFee = fee.trim();
        if (!Fee.isValidFee(trimmedFee)) {
            throw new ParseException(Fee.MESSAGE_CONSTRAINTS);
        }
        return new Fee(trimmedFee);
    }
}
