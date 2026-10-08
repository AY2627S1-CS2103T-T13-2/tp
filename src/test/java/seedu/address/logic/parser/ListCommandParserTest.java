package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;
import seedu.address.model.ListView;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_noView_returnsStudentListCommand() {
        assertParseSuccess(parser, "", new ListCommand(ListView.STUDENTS));
        assertParseSuccess(parser, "   ", new ListCommand(ListView.STUDENTS));
    }

    @Test
    public void parse_validView_returnsListCommand() {
        assertParseSuccess(parser, " students", new ListCommand(ListView.STUDENTS));
        assertParseSuccess(parser, " lessons", new ListCommand(ListView.LESSONS));
    }

    @Test
    public void parse_mixedCaseView_returnsListCommand() {
        assertParseSuccess(parser, " STUDENTS", new ListCommand(ListView.STUDENTS));
        assertParseSuccess(parser, " Lessons", new ListCommand(ListView.LESSONS));
    }

    @Test
    public void parse_unknownOrExtraArguments_throwsParseException() {
        assertParseFailure(parser, " teachers", ListCommand.MESSAGE_INVALID_VIEW);
        assertParseFailure(parser, " stu", ListCommand.MESSAGE_INVALID_VIEW);
        assertParseFailure(parser, " students 3", ListCommand.MESSAGE_INVALID_VIEW);
        assertParseFailure(parser, " all students", ListCommand.MESSAGE_INVALID_VIEW);
        assertParseFailure(parser, " s/12", ListCommand.MESSAGE_INVALID_VIEW);
    }

    @Test
    public void parse_viewGivenTwice_throwsParseException() {
        assertParseFailure(parser, " students students", ListCommand.MESSAGE_VIEW_GIVEN_TWICE);
        assertParseFailure(parser, " lessons LESSONS", ListCommand.MESSAGE_VIEW_GIVEN_TWICE);
    }
}
