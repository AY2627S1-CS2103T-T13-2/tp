package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_ID;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.StudentId;

public class ParserUtilTest {

    //// ids

    @Test
    public void isId() {
        assertTrue(ParserUtil.isId("1"));
        assertTrue(ParserUtil.isId("12"));
        assertTrue(ParserUtil.isId("007")); // leading zeros are allowed
        assertTrue(ParserUtil.isId("  12  ")); // surrounding spaces are ignored
        assertTrue(ParserUtil.isId("99999999999999999999")); // too large to store, but still an ID

        assertFalse(ParserUtil.isId("0"));
        assertFalse(ParserUtil.isId("000"));
        assertFalse(ParserUtil.isId("-1")); // a sign
        assertFalse(ParserUtil.isId("+1"));
        assertFalse(ParserUtil.isId("1.5")); // a decimal point
        assertFalse(ParserUtil.isId("12a"));
        assertFalse(ParserUtil.isId("1 2")); // a space inside
        assertFalse(ParserUtil.isId(""));
        assertFalse(ParserUtil.isId("Alice Tan"));
        assertThrows(NullPointerException.class, () -> ParserUtil.isId(null));
    }

    @Test
    public void parseStudentId_validInput_success() throws Exception {
        assertEquals(new StudentId(12), ParserUtil.parseStudentId("12"));
        assertEquals(new StudentId(7), ParserUtil.parseStudentId("007")); // leading zeros are ignored
        assertEquals(new StudentId(12), ParserUtil.parseStudentId("  12  ")); // spaces are trimmed
    }

    @Test
    public void parseStudentId_tooLargeToBeAnId_returnsTheLargestId() throws Exception {
        assertEquals(new StudentId(Integer.MAX_VALUE), ParserUtil.parseStudentId("99999999999999999999"));
        assertEquals(new StudentId(Integer.MAX_VALUE), ParserUtil.parseStudentId("2147483648"));
        assertEquals(new StudentId(Integer.MAX_VALUE), ParserUtil.parseStudentId("2147483647"));
    }

    @Test
    public void parseStudentId_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_ID, () -> ParserUtil.parseStudentId("0"));
        assertThrows(ParseException.class, MESSAGE_INVALID_ID, () -> ParserUtil.parseStudentId("-1"));
        assertThrows(ParseException.class, MESSAGE_INVALID_ID, () -> ParserUtil.parseStudentId("abc"));
        assertThrows(ParseException.class, MESSAGE_INVALID_ID, () -> ParserUtil.parseStudentId(""));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseStudentId(null));
    }

    @Test
    public void parseLessonId() throws Exception {
        assertEquals(new LessonId(7), ParserUtil.parseLessonId(" 7 "));
        assertEquals(new LessonId(Integer.MAX_VALUE), ParserUtil.parseLessonId("99999999999"));
        assertThrows(ParseException.class, MESSAGE_INVALID_ID, () -> ParserUtil.parseLessonId("0"));
        assertThrows(ParseException.class, MESSAGE_INVALID_ID, () -> ParserUtil.parseLessonId("seven"));
    }

    //// name, contact, levels, subject

    @Test
    public void parseName() throws Exception {
        assertEquals(new Name("Alice Tan"), ParserUtil.parseName("  Alice Tan  ")); // trimmed
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName(""));
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName("12345"));
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName("R@chel"));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName(null));
    }

    @Test
    public void parseContact() throws Exception {
        assertEquals(new Contact("91234567"), ParserUtil.parseContact("  91234567 "));
        assertThrows(ParseException.class, Contact.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseContact(""));
        assertThrows(ParseException.class, Contact.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseContact("9"));
        assertThrows(ParseException.class, Contact.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseContact("phone"));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseContact(null));
    }

    @Test
    public void parseStudentLevel() throws Exception {
        assertEquals(new Level("Primary 6"), ParserUtil.parseStudentLevel("  primary   6 "));
        assertThrows(ParseException.class, Level.MESSAGE_CONSTRAINTS_STUDENT, () ->
                ParserUtil.parseStudentLevel(""));
        assertThrows(ParseException.class, Level.MESSAGE_CONSTRAINTS_STUDENT, () ->
                ParserUtil.parseStudentLevel("---"));
        assertThrows(ParseException.class, Level.MESSAGE_CONSTRAINTS_STUDENT, () ->
                ParserUtil.parseStudentLevel("a".repeat(31)));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseStudentLevel(null));
    }

    @Test
    public void parseLessonLevel_usesTheLessonMessage() throws Exception {
        assertEquals(new Level("Primary 6"), ParserUtil.parseLessonLevel("Primary 6"));
        assertThrows(ParseException.class, Level.MESSAGE_CONSTRAINTS_LESSON, () -> ParserUtil.parseLessonLevel(""));
        assertFalse(Level.MESSAGE_CONSTRAINTS_LESSON.equals(Level.MESSAGE_CONSTRAINTS_STUDENT));
    }

    @Test
    public void parseSubject() throws Exception {
        assertEquals(new Subject("Mathematics"), ParserUtil.parseSubject("  Mathematics "));
        assertThrows(ParseException.class, Subject.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseSubject(""));
        assertThrows(ParseException.class, Subject.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseSubject("101"));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseSubject(null));
    }

    //// day and times

    @Test
    public void parseDay() throws Exception {
        assertEquals(DayOfWeek.MONDAY, ParserUtil.parseDay("monday"));
        assertEquals(DayOfWeek.SATURDAY, ParserUtil.parseDay(" SATURDAY "));
        assertThrows(ParseException.class, Timeslot.MESSAGE_INVALID_DAY, () -> ParserUtil.parseDay("Mon"));
        assertThrows(ParseException.class, Timeslot.MESSAGE_INVALID_DAY, () -> ParserUtil.parseDay(""));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseDay(null));
    }

    @Test
    public void parseStartTime() throws Exception {
        assertEquals(LocalTime.of(9, 0), ParserUtil.parseStartTime("09:00"));
        assertThrows(ParseException.class, Timeslot.MESSAGE_INVALID_START_TIME, () ->
            ParserUtil.parseStartTime("9:00"));
        assertThrows(ParseException.class, Timeslot.MESSAGE_INVALID_START_TIME, () ->
                ParserUtil.parseStartTime("24:00"));
        assertThrows(ParseException.class, Timeslot.MESSAGE_INVALID_START_TIME, () -> ParserUtil.parseStartTime(""));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseStartTime(null));
    }

    @Test
    public void parseEndTime() throws Exception {
        LocalTime start = LocalTime.of(15, 0);

        assertEquals(LocalTime.of(16, 30), ParserUtil.parseEndTime("16:30", start));
        assertEquals(LocalTime.of(15, 1), ParserUtil.parseEndTime("15:01", start)); // one minute later is enough
    }

    @Test
    public void parseEndTime_notLaterThanStart_throwsParseException() {
        LocalTime start = LocalTime.of(15, 0);

        assertThrows(ParseException.class, Timeslot.MESSAGE_INVALID_END_TIME, () ->
            ParserUtil.parseEndTime("15:00", start));
        assertThrows(ParseException.class, Timeslot.MESSAGE_INVALID_END_TIME, () ->
            ParserUtil.parseEndTime("14:59", start));
    }

    @Test
    public void parseEndTime_wrongFormat_throwsParseException() {
        LocalTime start = LocalTime.of(15, 0);

        assertThrows(ParseException.class, Timeslot.MESSAGE_INVALID_END_TIME, () ->
            ParserUtil.parseEndTime("4:30", start));
        assertThrows(ParseException.class, Timeslot.MESSAGE_INVALID_END_TIME, () -> ParserUtil.parseEndTime("", start));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEndTime(null, start));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEndTime("16:00", null));
    }

    //// fee

    @Test
    public void parseFee() throws Exception {
        assertEquals(new Fee("50.00"), ParserUtil.parseFee(" 50 "));
        assertEquals(new Fee("50.50"), ParserUtil.parseFee("50.5"));
        assertThrows(ParseException.class, Fee.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseFee("0"));
        assertThrows(ParseException.class, Fee.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseFee("50.001"));
        assertThrows(ParseException.class, Fee.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseFee("$50"));
        assertThrows(ParseException.class, Fee.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseFee(""));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseFee(null));
    }
}
