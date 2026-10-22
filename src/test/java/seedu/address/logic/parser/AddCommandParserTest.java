package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PARAMETER;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.AddCommandParser.MESSAGE_LESSON_PARAMETERS_NOT_ALLOWED;
import static seedu.address.logic.parser.AddCommandParser.MESSAGE_MISSING_PARAMETER_FORMAT;
import static seedu.address.logic.parser.AddCommandParser.MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddCommand.ExistingLesson;
import seedu.address.logic.commands.AddCommand.ExistingStudent;
import seedu.address.logic.commands.AddCommand.LessonRef;
import seedu.address.logic.commands.AddCommand.NewLesson;
import seedu.address.logic.commands.AddCommand.NewStudent;
import seedu.address.logic.commands.AddCommand.StudentRef;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.StudentId;

public class AddCommandParserTest {

    private static final String USAGE_MESSAGE = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

    // The four ways of writing each side of the command.
    private static final String EXISTING_STUDENT = " s/12";
    private static final String NEW_STUDENT = " s/Alice Tan c/91234567 slv/Primary 6";
    private static final String EXISTING_LESSON = " l/7";
    private static final String NEW_LESSON = " l/Mathematics llv/Primary 6 d/Monday st/15:00 et/16:30 f/50.00";

    // What each of them should parse to.
    private static final StudentRef EXISTING_STUDENT_REF = new ExistingStudent(new StudentId(12));
    private static final StudentRef NEW_STUDENT_REF =
            new NewStudent(new Name("Alice Tan"), new Contact("91234567"), new Level("Primary 6"));
    private static final LessonRef EXISTING_LESSON_REF = new ExistingLesson(new LessonId(7));
    private static final LessonRef NEW_LESSON_REF = new NewLesson(new Subject("Mathematics"), new Level("Primary 6"),
            new Timeslot(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)), new Fee("50.00"));

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    //// the four forms

    @Test
    public void parse_existingStudentAndExistingLesson_success() {
        assertParseSuccess(parser, EXISTING_STUDENT + EXISTING_LESSON,
                new AddCommand(EXISTING_STUDENT_REF, EXISTING_LESSON_REF));
    }

    @Test
    public void parse_newStudentAndExistingLesson_success() {
        assertParseSuccess(parser, NEW_STUDENT + EXISTING_LESSON,
                new AddCommand(NEW_STUDENT_REF, EXISTING_LESSON_REF));
    }

    @Test
    public void parse_existingStudentAndNewLesson_success() {
        assertParseSuccess(parser, EXISTING_STUDENT + NEW_LESSON,
                new AddCommand(EXISTING_STUDENT_REF, NEW_LESSON_REF));
    }

    @Test
    public void parse_newStudentAndNewLesson_success() {
        assertParseSuccess(parser, NEW_STUDENT + NEW_LESSON, new AddCommand(NEW_STUDENT_REF, NEW_LESSON_REF));
    }

    @Test
    public void parse_specificationExamples_success() {
        assertParseSuccess(parser, " s/12 l/7", new AddCommand(EXISTING_STUDENT_REF, EXISTING_LESSON_REF));
        assertParseSuccess(parser, " s/Alice Tan c/91234567 slv/Primary 6 l/7",
                new AddCommand(NEW_STUDENT_REF, EXISTING_LESSON_REF));
        assertParseSuccess(parser, " s/12 l/Mathematics llv/Primary 6 d/Monday st/15:00 et/16:30 f/50.00",
                new AddCommand(EXISTING_STUDENT_REF, NEW_LESSON_REF));
        assertParseSuccess(parser, " s/Alice Tan c/91234567 slv/Primary 6 l/Mathematics llv/Primary 6 d/Monday "
                + "st/15:00 et/16:30 f/50.00", new AddCommand(NEW_STUDENT_REF, NEW_LESSON_REF));
    }

    //// how values are read

    @Test
    public void parse_parametersInAnyOrder_success() {
        String shuffled = " st/15:00 f/50.00 l/Mathematics et/16:30 slv/Primary 6 s/Alice Tan d/Monday "
                + "c/91234567 llv/Primary 6";

        assertParseSuccess(parser, shuffled, new AddCommand(NEW_STUDENT_REF, NEW_LESSON_REF));
    }

    @Test
    public void parse_extraSpacesAroundValues_success() {
        String spaced = "   s/Alice Tan    c/91234567   slv/   primary    6   l/Mathematics  llv/PRIMARY 6 "
                + "d/monday    st/15:00   et/16:30  f/50   ";

        assertParseSuccess(parser, spaced, new AddCommand(NEW_STUDENT_REF, NEW_LESSON_REF));
    }

    @Test
    public void parse_dayIsCaseInsensitive_success() {
        assertParseSuccess(parser, EXISTING_STUDENT + " l/Mathematics llv/Primary 6 d/mOnDaY st/15:00 et/16:30 f/50",
                new AddCommand(EXISTING_STUDENT_REF, NEW_LESSON_REF));
    }

    @Test
    public void parse_idsWithLeadingZeros_areTheSameAsWithout() {
        assertParseSuccess(parser, " s/0012 l/007", new AddCommand(EXISTING_STUDENT_REF, EXISTING_LESSON_REF));
    }

    @Test
    public void parse_nameThatStartsWithDigits_isANewStudent() {
        StudentRef student = new NewStudent(new Name("12abc"), new Contact("91234567"), new Level("Primary 6"));

        assertParseSuccess(parser, " s/12abc c/91234567 slv/Primary 6" + EXISTING_LESSON,
                new AddCommand(student, EXISTING_LESSON_REF));
    }

    @Test
    public void parse_feeWithoutDecimals_success() {
        assertParseSuccess(parser, EXISTING_STUDENT + " l/Mathematics llv/Primary 6 d/Monday st/15:00 et/16:30 f/50.5",
                new AddCommand(EXISTING_STUDENT_REF, new NewLesson(new Subject("Mathematics"), new Level("Primary 6"),
                        new Timeslot(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)), new Fee("50.50"))));
    }

    //// malformed commands

    @Test
    public void parse_noParameters_throwsParseException() {
        assertParseFailure(parser, "", USAGE_MESSAGE);
        assertParseFailure(parser, "     ", USAGE_MESSAGE);
    }

    @Test
    public void parse_textBeforeTheFirstParameter_throwsParseException() {
        assertParseFailure(parser, " hello" + EXISTING_STUDENT + EXISTING_LESSON, USAGE_MESSAGE);
        assertParseFailure(parser, " 12 7", USAGE_MESSAGE);
    }

    @Test
    public void parse_parameterGivenTwice_throwsParseException() {
        assertParseFailure(parser, EXISTING_STUDENT + EXISTING_LESSON + " s/13", MESSAGE_DUPLICATE_PARAMETER);
        assertParseFailure(parser, EXISTING_STUDENT + EXISTING_LESSON + " l/8", MESSAGE_DUPLICATE_PARAMETER);

        // every parameter, even one whose repeated value would be invalid, is reported as given twice
        String complete = NEW_STUDENT + NEW_LESSON;
        for (String prefix : new String[] {"s/", "l/", "c/", "slv/", "llv/", "d/", "st/", "et/", "f/"}) {
            assertParseFailure(parser, complete + " " + prefix + "!", MESSAGE_DUPLICATE_PARAMETER);
        }
    }

    //// missing parameters

    @Test
    public void parse_studentOrLessonMissing_throwsParseException() {
        assertParseFailure(parser, EXISTING_LESSON, missing("s/"));
        assertParseFailure(parser, EXISTING_STUDENT, missing("l/"));
        assertParseFailure(parser, " c/91234567 slv/Primary 6", missing("s/, l/"));
    }

    @Test
    public void parse_newStudentWithMissingParameters_throwsParseException() {
        assertParseFailure(parser, " s/Alice Tan" + EXISTING_LESSON, missing("c/, slv/"));
        assertParseFailure(parser, " s/Alice Tan slv/Primary 6" + EXISTING_LESSON, missing("c/"));
        assertParseFailure(parser, " s/Alice Tan c/91234567" + EXISTING_LESSON, missing("slv/"));
    }

    @Test
    public void parse_newLessonWithMissingParameters_throwsParseException() {
        assertParseFailure(parser, EXISTING_STUDENT + " l/Mathematics", missing("llv/, d/, st/, et/, f/"));

        String[] lessonParameters = {" llv/Primary 6", " d/Monday", " st/15:00", " et/16:30", " f/50.00"};
        String[] prefixes = {"llv/", "d/", "st/", "et/", "f/"};
        for (int omitted = 0; omitted < lessonParameters.length; omitted++) {
            StringBuilder input = new StringBuilder(EXISTING_STUDENT + " l/Mathematics");
            for (int i = 0; i < lessonParameters.length; i++) {
                if (i != omitted) {
                    input.append(lessonParameters[i]);
                }
            }
            assertParseFailure(parser, input.toString(), missing(prefixes[omitted]));
        }
    }

    @Test
    public void parse_newStudentAndNewLessonWithMissingParameters_listsAllInOrder() {
        assertParseFailure(parser, " s/Alice Tan l/Mathematics", missing("c/, slv/, llv/, d/, st/, et/, f/"));
        assertParseFailure(parser, " s/Alice Tan c/91234567 l/Mathematics llv/Primary 6 d/Monday st/15:00",
                missing("slv/, et/, f/"));
    }

    //// parameters that do not belong

    @Test
    public void parse_existingStudentWithNewStudentParameters_throwsParseException() {
        assertParseFailure(parser, EXISTING_STUDENT + EXISTING_LESSON + " c/91234567",
                MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED);
        assertParseFailure(parser, EXISTING_STUDENT + EXISTING_LESSON + " slv/Primary 6",
                MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED);
    }

    @Test
    public void parse_existingLessonWithNewLessonParameters_throwsParseException() {
        for (String parameter : new String[] {" llv/Primary 6", " d/Monday", " st/15:00", " et/16:30", " f/50.00"}) {
            assertParseFailure(parser, EXISTING_STUDENT + EXISTING_LESSON + parameter,
                    MESSAGE_LESSON_PARAMETERS_NOT_ALLOWED);
            assertParseFailure(parser, NEW_STUDENT + EXISTING_LESSON + parameter,
                    MESSAGE_LESSON_PARAMETERS_NOT_ALLOWED);
        }
    }

    @Test
    public void parse_bothSidesHaveParametersThatDoNotBelong_reportsTheStudentFirst() {
        assertParseFailure(parser, EXISTING_STUDENT + EXISTING_LESSON + " c/91234567 llv/Primary 6",
                MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED);
    }

    @Test
    public void parse_parameterThatDoesNotBelong_isReportedBeforeAMissingOne() {
        // the existing student must not have a contact, even though the new lesson is also incomplete
        assertParseFailure(parser, EXISTING_STUDENT + " l/Mathematics c/91234567",
                MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED);
    }

    //// invalid values

    @Test
    public void parse_invalidStudentValues_throwsParseException() {
        assertParseFailure(parser, " s/R@chel c/91234567 slv/Primary 6" + EXISTING_LESSON,
                Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " s/0 c/91234567 slv/Primary 6" + EXISTING_LESSON,
                Name.MESSAGE_CONSTRAINTS); // zero is not an ID, and it has no letter to be a name
        assertParseFailure(parser, " s/" + "a".repeat(101) + " c/91234567 slv/Primary 6" + EXISTING_LESSON,
                Name.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, " s/Alice Tan c/12 slv/Primary 6" + EXISTING_LESSON,
                Contact.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " s/Alice Tan c/phone slv/Primary 6" + EXISTING_LESSON,
                Contact.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, " s/Alice Tan c/91234567 slv/---" + EXISTING_LESSON,
                Level.MESSAGE_CONSTRAINTS_STUDENT);
        assertParseFailure(parser, " s/Alice Tan c/91234567 slv/" + "a".repeat(31) + EXISTING_LESSON,
                Level.MESSAGE_CONSTRAINTS_STUDENT);
    }

    @Test
    public void parse_invalidLessonValues_throwsParseException() {
        String prefix = EXISTING_STUDENT + " l/";
        String rest = " llv/Primary 6 d/Monday st/15:00 et/16:30 f/50.00";

        assertParseFailure(parser, prefix + "Maths!" + rest, Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, prefix + "---" + rest, Subject.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, EXISTING_STUDENT + " l/Mathematics llv/--- d/Monday st/15:00 et/16:30 f/50",
                Level.MESSAGE_CONSTRAINTS_LESSON);
        assertParseFailure(parser, EXISTING_STUDENT + " l/Mathematics llv/Primary 6 d/Mon st/15:00 et/16:30 f/50",
                Timeslot.MESSAGE_INVALID_DAY);
    }

    @Test
    public void parse_invalidTimes_throwsParseException() {
        String start = EXISTING_STUDENT + " l/Mathematics llv/Primary 6 d/Monday st/";

        assertParseFailure(parser, start + "9:00 et/16:30 f/50", Timeslot.MESSAGE_INVALID_START_TIME);
        assertParseFailure(parser, start + "24:00 et/16:30 f/50", Timeslot.MESSAGE_INVALID_START_TIME);

        assertParseFailure(parser, start + "15:00 et/15:00 f/50", Timeslot.MESSAGE_INVALID_END_TIME); // not later
        assertParseFailure(parser, start + "15:00 et/14:00 f/50", Timeslot.MESSAGE_INVALID_END_TIME); // earlier
        assertParseFailure(parser, start + "15:00 et/4:30 f/50", Timeslot.MESSAGE_INVALID_END_TIME); // wrong format
    }

    @Test
    public void parse_invalidFee_throwsParseException() {
        String lesson = EXISTING_STUDENT + " l/Mathematics llv/Primary 6 d/Monday st/15:00 et/16:30 f/";

        assertParseFailure(parser, lesson + "0", Fee.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, lesson + "50.001", Fee.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, lesson + "$50", Fee.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, lesson + "-5", Fee.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_severalInvalidValues_reportsTheStudentBeforeTheLesson() {
        // an invalid contact and an invalid fee: the contact comes first
        assertParseFailure(parser, " s/Alice Tan c/12 slv/Primary 6 l/Mathematics llv/Primary 6 d/Monday st/15:00 "
                + "et/16:30 f/0", Contact.MESSAGE_CONSTRAINTS);

        // an invalid student name and an invalid subject: the student comes first
        assertParseFailure(parser, " s/R@chel c/91234567 slv/Primary 6 l/Maths! llv/Primary 6 d/Monday st/15:00 "
                + "et/16:30 f/50", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidStartTimeAndEndTime_reportsTheStartTime() {
        assertParseFailure(parser, EXISTING_STUDENT + " l/Mathematics llv/Primary 6 d/Monday st/3pm et/16:30 f/50",
                Timeslot.MESSAGE_INVALID_START_TIME);
    }

    private static String missing(String prefixes) {
        return String.format(MESSAGE_MISSING_PARAMETER_FORMAT, prefixes);
    }
}
