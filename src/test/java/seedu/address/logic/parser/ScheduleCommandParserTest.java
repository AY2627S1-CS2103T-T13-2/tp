package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_ID_TOO_LARGE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_ID;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ScheduleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudentId;

class ScheduleCommandParserTest {

    private final ScheduleCommandParser parser = new ScheduleCommandParser();

    @Test
    void parse_noArguments_returnsScheduleCommand() throws Exception {
        assertEquals(new ScheduleCommand(), parser.parse(""));
        assertEquals(new ScheduleCommand(), parser.parse("  "));
    }

    @Test
    void parse_studentId_returnsStudentScheduleCommand() throws Exception {
        assertEquals(new ScheduleCommand(new StudentId(1)), parser.parse(" student 1 "));
        assertEquals(new ScheduleCommand(new StudentId(1)), parser.parse(" STUDENT 001 "));
    }

    @Test
    void parse_unknownKeyword_reportsUnknownParameter() {
        assertThrows(ParseException.class, ScheduleCommand.MESSAGE_UNKNOWN_PARAMETER, () ->
                parser.parse(" /week 2026-09-14"));
        assertThrows(ParseException.class, ScheduleCommand.MESSAGE_UNKNOWN_PARAMETER, () ->
                parser.parse(" unexpected"));
    }

    @Test
    void parse_missingOrExtraTokens_reportsInvalidFormat() {
        assertThrows(ParseException.class, ScheduleCommand.MESSAGE_INVALID_FORMAT, () ->
                parser.parse(" student"));
        assertThrows(ParseException.class, ScheduleCommand.MESSAGE_INVALID_FORMAT, () ->
                parser.parse(" student student 1"));
        assertThrows(ParseException.class, ScheduleCommand.MESSAGE_INVALID_FORMAT, () ->
                parser.parse(" student 1 extra"));
    }

    @Test
    void parse_invalidId_reportsSharedIdError() {
        assertThrows(ParseException.class, MESSAGE_INVALID_ID, () -> parser.parse(" student abc"));
        assertThrows(ParseException.class, MESSAGE_INVALID_ID, () -> parser.parse(" student 0"));
        assertThrows(ParseException.class, MESSAGE_ID_TOO_LARGE, () -> parser.parse(" student 2147483648"));
    }
}
