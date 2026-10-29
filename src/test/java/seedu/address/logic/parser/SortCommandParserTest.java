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
        // Students
        assertParseSuccess(parser, "student name", new SortCommand("student", "name",
                SortField.STUDENT_NAME, true));
        assertParseSuccess(parser, "student name asc", new SortCommand("student", "name",
                SortField.STUDENT_NAME, true));
        assertParseSuccess(parser, "student name desc", new SortCommand("student", "name",
                SortField.STUDENT_NAME, false));
        assertParseSuccess(parser, "student payment", new SortCommand("student", "payment",
                SortField.STUDENT_PAYMENT, true));
        assertParseSuccess(parser, "student paid", new SortCommand("student", "payment",
                SortField.STUDENT_PAYMENT, true));
        assertParseSuccess(parser, "student unpaid", new SortCommand("student", "payment",
                SortField.STUDENT_PAYMENT, true));
        assertParseSuccess(parser, "student paid desc", new SortCommand("student", "payment",
                SortField.STUDENT_PAYMENT, false));
        assertParseSuccess(parser, "student id", new SortCommand("student", "id",
                SortField.STUDENT_ID, true));
        assertParseSuccess(parser, "student level", new SortCommand("student", "level",
                SortField.STUDENT_LEVEL, true));
        // Lessons
        assertParseSuccess(parser, "lesson fee desc", new SortCommand("lesson", "fee",
                SortField.LESSON_FEE, false));
        assertParseSuccess(parser, "lesson subject", new SortCommand("lesson", "subject",
                SortField.LESSON_SUBJECT, true));
        assertParseSuccess(parser, "lesson time asc", new SortCommand("lesson", "time",
                SortField.LESSON_TIME, true));
        assertParseSuccess(parser, "lesson id", new SortCommand("lesson", "id",
                SortField.LESSON_ID, true));
        assertParseSuccess(parser, "lesson level desc", new SortCommand("lesson", "level",
                SortField.LESSON_LEVEL, false));

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
        assertParseFailure(parser, "student", message);
        assertParseFailure(parser, "student fee", message);
        assertParseFailure(parser, "lesson address", message);
        assertParseFailure(parser, "lesson time sideways", message);
        assertParseFailure(parser, "student name asc extra", message);
    }
}
