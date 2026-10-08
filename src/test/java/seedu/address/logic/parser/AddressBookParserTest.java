package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Checks that every command of the MVP is registered. What each command does with its arguments is tested in the
 * test class of that command.
 */
public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        assertTrue(parser.parseCommand("add s/12 l/7") instanceof AddCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        assertTrue(parser.parseCommand("delete student 12") instanceof DeleteCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand("list") instanceof ListCommand);
        assertTrue(parser.parseCommand("list lessons") instanceof ListCommand);
    }

    @Test
    public void parseCommand_mark() throws Exception {
        assertTrue(parser.parseCommand("mark 12 2026-10") instanceof MarkCommand);
    }

    @Test
    public void parseCommand_filter() throws Exception {
        assertTrue(parser.parseCommand("filter student 12") instanceof FilterCommand);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_leadingAndTrailingSpaces_areIgnored() throws Exception {
        assertTrue(parser.parseCommand("   list   ") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.COMMAND_WORD);

        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(""));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand("  "));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_commandWordIsCaseSensitive() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("LIST"));
    }

    @Test
    public void parseCommand_commandsRemovedFromTheAddressBook_areUnknown() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("edit 1 n/Bob"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("find alice"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("clear"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("remark 1 r/hi"));
    }
}
