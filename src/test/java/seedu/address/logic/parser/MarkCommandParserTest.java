package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.model.student.StudentId;

public class MarkCommandParserTest {

    private final MarkCommandParser parser = new MarkCommandParser();

    @Test
    public void parse_validArguments_returnsMarkCommand() {
        assertParseSuccess(parser, "1", new MarkCommand(new StudentId(1)));
    }

    @Test
    public void parse_wrongNumberOfArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "1 extra", expectedMessage);
    }

    @Test
    public void parse_invalidId_throwsParseException() {
        assertParseFailure(parser, "zero", ParserUtil.MESSAGE_INVALID_ID);
        assertParseFailure(parser, "0", ParserUtil.MESSAGE_INVALID_ID);
    }
}
