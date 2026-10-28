package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETER;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.EditCommand.MESSAGE_LESSON_PARAMETERS_NOT_ALLOWED;
import static seedu.address.logic.commands.EditCommand.MESSAGE_NO_FIELDS;
import static seedu.address.logic.commands.EditCommand.MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED;
import static seedu.address.logic.parser.ArgumentMultimap.MESSAGE_PREFIX_INSIDE_VALUE;
import static seedu.address.logic.parser.ArgumentTokenizer.MESSAGE_UNKNOWN_PARAMETER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_ID_TOO_LARGE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_ID;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.LessonEdit;
import seedu.address.logic.commands.EditCommand.StudentEdit;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.StudentId;

public class EditCommandParserTest {

    private final EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_studentFields_returnsEditCommand() {
        StudentEdit edit = new StudentEdit(new StudentId(3), Optional.of(new Name("Ravi s/o Kumar")),
                Optional.of(new Contact("+65 (9123) 4567")), Optional.of(new Level("Secondary 1")));

        assertParseSuccess(parser, " student 3 C/+65 (9123) 4567 SLV/Secondary 1 S/Ravi s/o Kumar",
                new EditCommand(edit));
    }

    @Test
    public void parse_studentNameStartingWithO_returnsEditCommand() {
        StudentEdit singleLetterName = new StudentEdit(new StudentId(1), Optional.of(new Name("o")),
                Optional.empty(), Optional.empty());
        StudentEdit nameStartingWithO = new StudentEdit(new StudentId(1), Optional.of(new Name("o Tan")),
                Optional.empty(), Optional.empty());

        assertParseSuccess(parser, " student 1 s/o", new EditCommand(singleLetterName));
        assertParseSuccess(parser, " student 1 s/o Tan", new EditCommand(nameStartingWithO));
        assertParseSuccess(parser, " student 1 c/90001111 s/o Tan",
                new EditCommand(new StudentEdit(new StudentId(1), Optional.of(new Name("o Tan")),
                        Optional.of(new Contact("90001111")), Optional.empty())));
    }

    @Test
    public void parse_lessonFields_returnsEditCommand() {
        LessonEdit edit = new LessonEdit(new LessonId(2), Optional.of(new Subject("Physics")),
                Optional.of(new Level("Primary 6")), Optional.of(DayOfWeek.FRIDAY),
                Optional.of(LocalTime.of(9, 0)), Optional.of(LocalTime.of(15, 30)),
                Optional.of(new Fee("0")));

        assertParseSuccess(parser,
                " LESSON 2 F/0 ET/15:30 L/Physics D/Fri ST/9:00 LLV/Primary 6",
                new EditCommand(edit));
    }

    @Test
    public void parse_kindIsCaseInsensitiveAndLeadingZeroIsAllowed_returnsEditCommand() {
        StudentEdit edit = new StudentEdit(new StudentId(3), Optional.empty(),
                Optional.of(new Contact("90001111")), Optional.empty());

        assertParseSuccess(parser, " STUDENT 003 c/90001111", new EditCommand(edit));
    }

    @Test
    public void parse_partialLessonTimes_returnsEditCommand() {
        LessonEdit edit = new LessonEdit(new LessonId(2), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.of(LocalTime.of(16, 0)), Optional.empty());

        assertParseSuccess(parser, " lesson 2 et/16:00", new EditCommand(edit));
    }

    @Test
    public void parse_lessonEndingAtMidnight_throwsSpecificParseException() {
        assertParseFailure(parser, " lesson 1 et/24:00", Timeslot.MESSAGE_MIDNIGHT_END);
    }

    @Test
    public void parse_noFields_throwsParseException() {
        assertParseFailure(parser, " student 1", MESSAGE_NO_FIELDS);
        assertParseFailure(parser, " lesson 1", MESSAGE_NO_FIELDS);
    }

    @Test
    public void parse_wrongKindParameters_throwsParseException() {
        assertParseFailure(parser, " student 1 l/Mathematics", MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED);
        assertParseFailure(parser, " lesson 1 c/91234567", MESSAGE_LESSON_PARAMETERS_NOT_ALLOWED);
    }

    @Test
    public void parse_repeatedParameter_throwsParseException() {
        assertParseFailure(parser, " student 1 c/91234567 c/98765432", MESSAGE_DUPLICATE_PARAMETER);
    }

    @Test
    public void parse_unknownParameter_throwsSpecificParseException() {
        String expectedMessage = String.format(MESSAGE_UNKNOWN_PARAMETER, "x/", EditCommand.MESSAGE_USAGE);

        assertParseFailure(parser, " student 1 s/Alicia x/value", expectedMessage);
        assertParseFailure(parser, " student 1 c/92345678 x/value", expectedMessage);
        assertParseFailure(parser, " lesson 1 f/55 x/value", expectedMessage);
    }

    @Test
    public void parse_prefixWithoutSeparatingSpace_throwsSpecificParseException() {
        assertParseFailure(parser, " student 1 c/92345678slv/Secondary 1",
                String.format(MESSAGE_PREFIX_INSIDE_VALUE, "slv/", "c/"));
        assertParseFailure(parser, " lesson 1 st/15:00et/16:00",
                String.format(MESSAGE_PREFIX_INSIDE_VALUE, "et/", "st/"));

        String invalidFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " student 1s/Alicia", invalidFormat);
    }

    @Test
    public void parse_invalidTarget_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "", message);
        assertParseFailure(parser, " teacher 1 s/Alice", message);
        assertParseFailure(parser, " student 1 extra s/Alice", message);
    }

    @Test
    public void parse_invalidId_throwsParseException() {
        assertParseFailure(parser, " student 0 s/Alice", MESSAGE_INVALID_ID);
        assertParseFailure(parser, " student -1 s/Alice", MESSAGE_INVALID_ID);
        assertParseFailure(parser, " lesson 1.5 f/50", MESSAGE_INVALID_ID);
        assertParseFailure(parser, " student 2147483648 s/Alice", MESSAGE_ID_TOO_LARGE);
        assertParseFailure(parser, " lesson 2147483648 f/50", MESSAGE_ID_TOO_LARGE);
    }
}
