package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_ID;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.lesson.LessonId;
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
    }
}
