package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.model.student.StudentId;

public class UnmarkCommandParserTest {
    private final UnmarkCommandParser parser = new UnmarkCommandParser();

    @Test
    public void parse_validArguments_returnsUnmarkCommand() {
        assertParseSuccess(parser, "1", new UnmarkCommand(new StudentId(1)));
    }

    @Test
    public void parse_wrongNumberOfArguments_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnmarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expected);
        assertParseFailure(parser, "1 extra", expected);
    }

    @Test
    public void parse_invalidId_throwsParseException() {
        assertParseFailure(parser, "zero", ParserUtil.MESSAGE_INVALID_ID);
        assertParseFailure(parser, "0", ParserUtil.MESSAGE_INVALID_ID);
    }
}
