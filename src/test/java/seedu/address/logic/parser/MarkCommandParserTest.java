package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

public class MarkCommandParserTest {

    private final MarkCommandParser parser = new MarkCommandParser();

    @Test
    public void parse_validArguments_returnsMarkCommand() {
        // Single student ID
        assertParseSuccess(parser, "1", new MarkCommand(new StudentId(1)));

        // Multiple student IDs
        assertParseSuccess(parser, "1 2 3",
                new MarkCommand(Arrays.asList(new StudentId(1), new StudentId(2), new StudentId(3))));

        // Lesson-wide marking
        assertParseSuccess(parser, "lesson 1", new MarkCommand(new LessonId(1)));
        assertParseSuccess(parser, "  lesson   2  ", new MarkCommand(new LessonId(2)));
    }

    @Test
    public void parse_invalidCommandFormat_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "   ", expectedMessage);
        assertParseFailure(parser, "lesson", expectedMessage);
        assertParseFailure(parser, "lesson 1 2", expectedMessage);
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
