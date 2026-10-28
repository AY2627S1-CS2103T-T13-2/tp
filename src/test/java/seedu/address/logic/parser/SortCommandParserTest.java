package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.model.SortField;

public class SortCommandParserTest {
    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_validForms_returnsCommand() {
        assertParseSuccess(parser, "student name", new SortCommand("student", "name",
                SortField.STUDENT_NAME, true));
        assertParseSuccess(parser, "lesson fee desc", new SortCommand("lesson", "fee",
                SortField.LESSON_FEE, false));

        // letter case is ignored
        assertParseSuccess(parser, "STUDENT NAME ASC", new SortCommand("student", "name",
                SortField.STUDENT_NAME, true));
        assertParseSuccess(parser, "LESSON FEE DESC", new SortCommand("lesson", "fee",
                SortField.LESSON_FEE, false));
    }

    @Test
    public void parse_invalidEntityFieldOrDirection_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", message);
        assertParseFailure(parser, "student fee", message);
        assertParseFailure(parser, "lesson time sideways", message);
    }
}
