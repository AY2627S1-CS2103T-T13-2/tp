package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ScheduleCommand;
import seedu.address.logic.parser.exceptions.ParseException;

class ScheduleCommandParserTest {

    private final ScheduleCommandParser parser = new ScheduleCommandParser();

    @Test
    void parse_noArguments_returnsScheduleCommand() throws Exception {
        assertEquals(new ScheduleCommand(), parser.parse(""));
        assertEquals(new ScheduleCommand(), parser.parse("  "));
    }

    @Test
    void parse_anyArgument_reportsUnknownParameter() {
        assertThrows(ParseException.class, ScheduleCommand.MESSAGE_UNKNOWN_PARAMETER, () ->
                parser.parse(" /week 2026-09-14"));
        assertThrows(ParseException.class, ScheduleCommand.MESSAGE_UNKNOWN_PARAMETER, () ->
                parser.parse(" unexpected"));
    }
}
