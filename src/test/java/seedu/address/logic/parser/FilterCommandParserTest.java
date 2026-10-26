package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETER;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
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

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);

    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_validStudentArgs_returnsFilterCommand() {
        assertParseSuccess(parser, "student 1", new FilterCommand(new StudentId(1)));

        // extra whitespace is ignored
        assertParseSuccess(parser, "  student   1  ", new FilterCommand(new StudentId(1)));
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
        assertParseFailure(parser, " day l/Mathematics", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " lesson 1 llv/Primary 6", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_missingOrExtraParts_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "student", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "student 1 extra", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unknownKeyword_throwsParseException() {
        assertParseFailure(parser, "students 1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "Student 1", MESSAGE_INVALID_FORMAT);
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
