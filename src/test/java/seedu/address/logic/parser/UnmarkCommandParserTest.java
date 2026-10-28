package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

public class UnmarkCommandParserTest {

    private final UnmarkCommandParser parser = new UnmarkCommandParser();

    @Test
    public void parse_validArguments_returnsUnmarkCommand() {
        // Single student ID
        assertParseSuccess(parser, "1", new UnmarkCommand(new StudentId(1)));

        // Multiple student IDs
        assertParseSuccess(parser, "1 2 3",
                new UnmarkCommand(Arrays.asList(new StudentId(1), new StudentId(2), new StudentId(3))));

        // Lesson-wide unmarking
        assertParseSuccess(parser, "lesson 1", new UnmarkCommand(new LessonId(1)));
        assertParseSuccess(parser, "  lesson   2  ", new UnmarkCommand(new LessonId(2)));
    }

    @Test
    public void parse_invalidCommandFormat_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expected);
        assertParseFailure(parser, "   ", expected);
        assertParseFailure(parser, "lesson", expected);
        assertParseFailure(parser, "lesson 1 2", expected);
    }

    @Test
    public void parse_invalidId_throwsParseException() {
        assertParseFailure(parser, "zero", ParserUtil.MESSAGE_INVALID_ID);
        assertParseFailure(parser, "0", ParserUtil.MESSAGE_INVALID_ID);
        assertParseFailure(parser, "2147483648", ParserUtil.MESSAGE_ID_TOO_LARGE);
        assertParseFailure(parser, "1 extra", ParserUtil.MESSAGE_INVALID_ID);
        assertParseFailure(parser, "lesson zero", ParserUtil.MESSAGE_INVALID_ID);
        assertParseFailure(parser, "lesson 0", ParserUtil.MESSAGE_INVALID_ID);
        assertParseFailure(parser, "lesson 2147483648", ParserUtil.MESSAGE_ID_TOO_LARGE);
    }
}
