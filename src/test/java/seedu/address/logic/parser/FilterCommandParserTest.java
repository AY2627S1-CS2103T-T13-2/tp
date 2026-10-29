package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_ID_TOO_LARGE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_ID;

import java.time.DayOfWeek;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.StudentId;

public class FilterCommandParserTest {

    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_validStudentArgs_returnsFilterCommand() {
        assertParseSuccess(parser, "student 1", new FilterCommand(new StudentId(1)));

        // extra whitespace is ignored
        assertParseSuccess(parser, "  student   1  ", new FilterCommand(new StudentId(1)));

        // letter case is ignored
        assertParseSuccess(parser, "STUDENT 1", new FilterCommand(new StudentId(1)));
        assertParseSuccess(parser, "Student 1", new FilterCommand(new StudentId(1)));
    }

    @Test
    public void parse_validLessonArgs_returnsFilterCommand() {
        assertParseSuccess(parser, "lesson 2", new FilterCommand(new LessonId(2)));
    }

    @Test
    public void parse_validDayArgs_returnsFilterCommand() {
        assertParseSuccess(parser, "day monday", new FilterCommand(DayOfWeek.MONDAY));

        // letter case and whitespace are ignored
        assertParseSuccess(parser, "  day   MONDAY  ", new FilterCommand(DayOfWeek.MONDAY));
    }

    @Test
    public void parse_validPaidArgs_returnsFilterCommand() {
        assertParseSuccess(parser, "paid", new FilterCommand(true));
        assertParseSuccess(parser, "unpaid", new FilterCommand(false));

        // extra whitespace is ignored
        assertParseSuccess(parser, "  paid  ", new FilterCommand(true));

        // letter case is ignored
        assertParseSuccess(parser, "PAID", new FilterCommand(true));
        assertParseSuccess(parser, "Paid", new FilterCommand(true));
        assertParseSuccess(parser, "UNPAID", new FilterCommand(false));
    }

    @Test
    public void parse_validSubjectAndLevelArgs_returnsFilterCommand() {
        Subject subject = new Subject("Additional Mathematics");
        Level level = new Level("Secondary 3");

        // subject only
        assertParseSuccess(parser, " l/Additional Mathematics", new FilterCommand(subject, null));

        // level only
        assertParseSuccess(parser, " llv/Secondary 3", new FilterCommand(null, level));

        // both, in either order
        assertParseSuccess(parser, " l/Additional Mathematics llv/Secondary 3", new FilterCommand(subject, level));
        assertParseSuccess(parser, " llv/Secondary 3 l/Additional Mathematics", new FilterCommand(subject, level));

        // no leading space
        assertParseSuccess(parser, "l/Additional Mathematics", new FilterCommand(subject, null));
    }

    @Test
    public void parse_validStudentLevelArgs_returnsFilterCommand() {
        Level level = new Level("Secondary 3");

        assertParseSuccess(parser, " slv/Secondary 3", new FilterCommand(level));

        // no leading space
        assertParseSuccess(parser, "slv/Secondary 3", new FilterCommand(level));
    }

    @Test
    public void parse_invalidStudentLevel_throwsParseException() {
        assertParseFailure(parser, " slv/", Level.MESSAGE_CONSTRAINTS_STUDENT);
        assertParseFailure(parser, " slv/Primary 6!", Level.MESSAGE_CONSTRAINTS_STUDENT);
    }

    @Test
    public void parse_repeatedStudentLevel_throwsParseException() {
        assertParseFailure(parser, " slv/Primary 6 slv/Primary 5", MESSAGE_DUPLICATE_PARAMETER);
    }

    @Test
    public void parse_studentLevelCombinedWithOtherForms_throwsParseException() {
        assertParseFailure(parser, " day slv/Primary 6", FilterCommandParser.MESSAGE_MIXED_FILTER_FORMS);
        assertParseFailure(parser, " lesson 1 slv/Primary 6", FilterCommandParser.MESSAGE_MIXED_FILTER_FORMS);
        assertParseFailure(parser, " l/Mathematics slv/Primary 6", FilterCommandParser.MESSAGE_MIXED_FILTER_FORMS);
        assertParseFailure(parser, " llv/Primary 6 slv/Primary 6", FilterCommandParser.MESSAGE_MIXED_FILTER_FORMS);
    }

    @Test
    public void parse_invalidSubjectOrLevel_throwsParseException() {
        assertParseFailure(parser, " l/", FilterCommandParser.MESSAGE_INVALID_SUBJECT);
        assertParseFailure(parser, " l/123", FilterCommandParser.MESSAGE_INVALID_SUBJECT);
        assertParseFailure(parser, " llv/", Level.MESSAGE_CONSTRAINTS_LESSON);
        assertParseFailure(parser, " l/Mathematics llv/Primary 6!", Level.MESSAGE_CONSTRAINTS_LESSON);
    }

    @Test
    public void parse_repeatedSubjectOrLevel_throwsParseException() {
        assertParseFailure(parser, " l/Mathematics l/Science", MESSAGE_DUPLICATE_PARAMETER);
        assertParseFailure(parser, " llv/Primary 6 llv/Primary 5", MESSAGE_DUPLICATE_PARAMETER);
    }

    @Test
    public void parse_textBeforeSubjectOrLevel_throwsParseException() {
        assertParseFailure(parser, " day l/Mathematics", FilterCommandParser.MESSAGE_MIXED_FILTER_FORMS);
        assertParseFailure(parser, " lesson 1 llv/Primary 6", FilterCommandParser.MESSAGE_MIXED_FILTER_FORMS);
    }

    @Test
    public void parse_missingOrExtraParts_throwsParseException() {
        assertParseFailure(parser, "", FilterCommandParser.MESSAGE_FILTER_EXAMPLES);
        assertParseFailure(parser, "student", String.format(FilterCommandParser.MESSAGE_MISSING_FILTER_VALUE,
                "student", "1"));
        assertParseFailure(parser, "1", String.format(FilterCommandParser.MESSAGE_UNKNOWN_FILTER, "1"));
        assertParseFailure(parser, "student 1 extra", String.format(FilterCommandParser.MESSAGE_EXTRA_FILTER_ARGUMENTS,
                "student", "1"));
    }

    @Test
    public void parse_unknownKeyword_throwsParseException() {
        assertParseFailure(parser, "students 1", String.format(FilterCommandParser.MESSAGE_UNKNOWN_FILTER,
                "students"));
        assertParseFailure(parser, "paid now", String.format(FilterCommandParser.MESSAGE_UNKNOWN_FILTER, "paid"));
    }

    @Test
    public void parse_invalidId_throwsParseException() {
        assertParseFailure(parser, "student abc", MESSAGE_INVALID_ID);
        assertParseFailure(parser, "lesson 0", MESSAGE_INVALID_ID);
        assertParseFailure(parser, "student 2147483648", MESSAGE_ID_TOO_LARGE);
        assertParseFailure(parser, "lesson 2147483648", MESSAGE_ID_TOO_LARGE);
    }

    @Test
    public void parse_invalidDay_throwsParseException() {
        assertParseFailure(parser, "day someday", Timeslot.MESSAGE_INVALID_DAY);
    }
}
