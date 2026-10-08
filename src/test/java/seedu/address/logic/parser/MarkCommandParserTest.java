package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.model.student.StudentId;

public class MarkCommandParserTest {

    private final MarkCommandParser parser = new MarkCommandParser();

    @Test
    public void parse_validArguments_returnsMarkCommand() {
        assertParseSuccess(parser, "1 2026-10",
                new MarkCommand(new StudentId(1), YearMonth.of(2026, 10)));
    }

    @Test
    public void parse_wrongNumberOfArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "1", expectedMessage);
        assertParseFailure(parser, "1 2026-10 extra", expectedMessage);
    }

    @Test
    public void parse_invalidId_throwsParseException() {
        assertParseFailure(parser, "zero 2026-10", ParserUtil.MESSAGE_INVALID_ID);
        assertParseFailure(parser, "0 2026-10", ParserUtil.MESSAGE_INVALID_ID);
    }

    @Test
    public void parse_invalidMonth_throwsParseException() {
        assertParseFailure(parser, "1 October-2026", "Month must be in YYYY-MM format.");
        assertParseFailure(parser, "1 2026-13", "Month must be in YYYY-MM format.");
    }
}
