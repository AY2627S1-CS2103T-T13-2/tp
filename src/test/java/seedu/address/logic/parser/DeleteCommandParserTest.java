package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

class DeleteCommandParserTest {

    private final DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    void parse_validStudentAndLessonCommands() throws Exception {
        assertEquals(new DeleteCommand(new StudentId(12)), parser.parse(" student 12 "));
        assertEquals(new DeleteCommand(new LessonId(7)), parser.parse("LESSON 7"));
        assertEquals(new DeleteCommand(new StudentId(12)), parser.parse("StUdEnT 0012"));
    }

    @Test
    void parse_invalidFormat_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        String[] invalidInputs = {"", "teacher 12", "stu 12", "student", "12", "student 0",
            "student -1", "student abc", "student 12 now", "student student 12", "lesson lesson 7"};

        for (String input : invalidInputs) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parse(input));
        }
    }
}

