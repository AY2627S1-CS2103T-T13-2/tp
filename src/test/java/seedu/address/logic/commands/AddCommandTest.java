package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;
import static seedu.address.logic.commands.AddCommand.MESSAGE_ALREADY_ENROLLED;
import static seedu.address.logic.commands.AddCommand.MESSAGE_LEVEL_MISMATCH;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalClock.CLOCK;
import static seedu.address.testutil.TypicalEnrollments.getTypicalEnrollments;
import static seedu.address.testutil.TypicalLessons.ENGLISH;
import static seedu.address.testutil.TypicalLessons.HISTORY;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.getTypicalLessons;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalStudents.getTypicalStudents;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand.ExistingLesson;
import seedu.address.logic.commands.AddCommand.ExistingStudent;
import seedu.address.logic.commands.AddCommand.LessonRef;
import seedu.address.logic.commands.AddCommand.NewLesson;
import seedu.address.logic.commands.AddCommand.NewStudent;
import seedu.address.logic.commands.AddCommand.StudentRef;
import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.common.Level;
import seedu.address.model.enrollment.Enrollment;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.testutil.LessonBuilder;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TuiTrackerBuilder;

public class AddCommandTest {

    private static final StudentRef EXISTING_STUDENT = new ExistingStudent(new StudentId(12));
    private static final StudentRef NEW_STUDENT =
            new NewStudent(new Name("Alice Tan"), new Contact("91234567"), new Level("Primary 6"));
    private static final LessonRef EXISTING_LESSON = new ExistingLesson(new LessonId(7));
    private static final Timeslot MONDAY_AFTERNOON =
            new Timeslot(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30));
    private static final LessonRef NEW_LESSON =
            new NewLesson(new Subject("Mathematics"), new Level("Primary 6"), MONDAY_AFTERNOON, new Fee("50.00"));

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null, EXISTING_LESSON));
        assertThrows(NullPointerException.class, () -> new AddCommand(EXISTING_STUDENT, null));
    }

    @Test
    public void references_nullDetails_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ExistingStudent(null));
        assertThrows(NullPointerException.class, () -> new ExistingLesson(null));
        assertThrows(NullPointerException.class, () -> new NewStudent(null, new Contact("91234567"),
                new Level("Primary 6")));
        assertThrows(NullPointerException.class, () -> new NewStudent(new Name("Alice Tan"), null,
                new Level("Primary 6")));
        assertThrows(NullPointerException.class, () -> new NewStudent(new Name("Alice Tan"),
                new Contact("91234567"), null));
        assertThrows(NullPointerException.class, () -> new NewLesson(null, new Level("Primary 6"),
                MONDAY_AFTERNOON, new Fee("50")));
        assertThrows(NullPointerException.class, () -> new NewLesson(new Subject("Mathematics"), null,
                MONDAY_AFTERNOON, new Fee("50")));
        assertThrows(NullPointerException.class, () -> new NewLesson(new Subject("Mathematics"),
                new Level("Primary 6"), null, new Fee("50")));
        assertThrows(NullPointerException.class, () -> new NewLesson(new Subject("Mathematics"),
                new Level("Primary 6"), MONDAY_AFTERNOON, null));
    }

    @Test
    public void equals() {
        AddCommand command = new AddCommand(NEW_STUDENT, NEW_LESSON);

        // same values
        assertTrue(command.equals(new AddCommand(NEW_STUDENT, NEW_LESSON)));

        // same object
        assertTrue(command.equals(command));

        // null, different type
        assertFalse(command.equals(null));
        assertFalse(command.equals(5));

        // different student, or different lesson
        assertFalse(command.equals(new AddCommand(EXISTING_STUDENT, NEW_LESSON)));
        assertFalse(command.equals(new AddCommand(NEW_STUDENT, EXISTING_LESSON)));
        assertFalse(command.equals(new AddCommand(new ExistingStudent(new StudentId(13)), NEW_LESSON)));
        assertFalse(command.equals(new AddCommand(NEW_STUDENT,
                new NewLesson(new Subject("Science"), new Level("Primary 6"), MONDAY_AFTERNOON, new Fee("50.00")))));
    }

    @Test
    public void hashCode_equalCommandsHaveEqualHashCodes() {
        assertEquals(new AddCommand(NEW_STUDENT, NEW_LESSON).hashCode(),
                new AddCommand(NEW_STUDENT, NEW_LESSON).hashCode());
    }

    @Test
    public void toStringMethod() {
        AddCommand command = new AddCommand(EXISTING_STUDENT, NEW_LESSON);
        String expected = AddCommand.class.getCanonicalName() + "{student=" + EXISTING_STUDENT + ", lesson="
                + NEW_LESSON + "}";
        assertEquals(expected, command.toString());
    }

    //// enrolling an existing student in an existing lesson

    @Test
    public void execute_unpaidStudent_enrollsAndReportsUnpaid() {
        Model model = modelWith(List.of(), List.of(HISTORY));
        Model expectedModel = modelWith(List.of(), List.of(HISTORY));
        expectedModel.enroll(ALICE.getId(), HISTORY.getId());

        assertCommandSuccess(enroll(ALICE.getId().value, HISTORY.getId().value), model,
                "Enrolled Alice Tan [Student ID: 1] in History [Lesson ID: 5]. Payment status: Unpaid.",
                expectedModel);
        assertTrue(model.isEnrolled(ALICE.getId(), HISTORY.getId()));
    }

    @Test
    public void execute_paidStudent_reportsPaid() {
        Lesson physics = new LessonBuilder().withId(5).withSubject("Physics").withLevel("Secondary 2")
                .withTimeslot(DayOfWeek.FRIDAY, LocalTime.of(14, 0), LocalTime.of(15, 0)).build();
        Model model = modelWith(List.of(), List.of(physics));
        Model expectedModel = modelWith(List.of(), List.of(physics));
        expectedModel.enroll(BRYAN.getId(), physics.getId());

        assertCommandSuccess(enroll(BRYAN.getId().value, physics.getId().value), model,
                "Enrolled Bryan Lim [Student ID: 2] in Physics [Lesson ID: 5]. Payment status: Paid.",
                expectedModel);
    }

    @Test
    public void execute_levelsDifferOnlyInCaseAndSpacing_success() {
        Lesson shoutedLevel = new LessonBuilder(HISTORY).withLevel("PRIMARY    6").build();
        Model model = modelWith(List.of(), List.of(shoutedLevel));
        Model expectedModel = modelWith(List.of(), List.of(shoutedLevel));
        expectedModel.enroll(ALICE.getId(), shoutedLevel.getId());

        assertCommandSuccess(enroll(ALICE.getId().value, shoutedLevel.getId().value), model,
                "Enrolled Alice Tan [Student ID: 1] in History [Lesson ID: 5]. Payment status: Unpaid.",
                expectedModel);
    }

    @Test
    public void execute_doesNotChangeWhichListIsShown() {
        Model model = modelWith(List.of(), List.of(HISTORY));
        model.setListView(ListView.LESSONS);
        Model expectedModel = modelWith(List.of(), List.of(HISTORY));
        expectedModel.setListView(ListView.LESSONS);
        expectedModel.enroll(ALICE.getId(), HISTORY.getId());

        assertCommandSuccess(enroll(ALICE.getId().value, HISTORY.getId().value), model,
                "Enrolled Alice Tan [Student ID: 1] in History [Lesson ID: 5]. Payment status: Unpaid.",
                expectedModel);
        assertEquals(ListView.LESSONS, model.getListView());
    }

    @Test
    public void execute_studentDoesNotExist_throwsCommandException() {
        Model model = modelWith(List.of(), List.of(HISTORY));

        assertCommandFailure(enroll(99, HISTORY.getId().value), model, String.format(MESSAGE_STUDENT_NOT_FOUND, 99));
    }

    @Test
    public void execute_lessonDoesNotExist_throwsCommandException() {
        Model model = modelWith(List.of(), List.of(HISTORY));

        assertCommandFailure(enroll(ALICE.getId().value, 99), model, String.format(MESSAGE_LESSON_NOT_FOUND, 99));
    }

    @Test
    public void execute_neitherExists_reportsTheStudent() {
        Model model = modelWith(List.of(), List.of());

        assertCommandFailure(enroll(98, 99), model, String.format(MESSAGE_STUDENT_NOT_FOUND, 98));
    }

    @Test
    public void execute_idOfTheOtherKind_isNotFound() {
        // student 4 and lesson 5 do not exist, although lesson 4 and student 5 might: IDs are numbered separately
        Student studentFive = new StudentBuilder().withId(5).withName("Eve Ong").withContact("90001111").build();
        Model model = modelWith(List.of(studentFive), List.of());

        assertCommandFailure(enroll(4, ENGLISH.getId().value), model, String.format(MESSAGE_STUDENT_NOT_FOUND, 4));
        assertCommandFailure(enroll(5, 5), model, String.format(MESSAGE_LESSON_NOT_FOUND, 5));
    }

    @Test
    public void execute_alreadyEnrolled_throwsCommandException() {
        Model model = modelWith(List.of(), List.of());

        assertCommandFailure(enroll(ALICE.getId().value, MATHEMATICS.getId().value), model,
                MESSAGE_ALREADY_ENROLLED);
    }

    @Test
    public void execute_levelsDoNotMatch_throwsCommandException() {
        Model model = modelWith(List.of(), List.of());

        // ALICE is Primary 6 and ENGLISH is Primary 4
        assertCommandFailure(enroll(ALICE.getId().value, ENGLISH.getId().value), model, MESSAGE_LEVEL_MISMATCH);
    }

    @Test
    public void execute_abbreviatedLevel_isADifferentLevel() {
        Student abbreviated = new StudentBuilder(ALICE).withId(5).withName("Eve Ong").withContact("90001111")
                .withLevel("P6").build();
        Model model = modelWith(List.of(abbreviated), List.of(HISTORY)); // HISTORY is "Primary 6"

        assertCommandFailure(enroll(abbreviated.getId().value, HISTORY.getId().value), model,
                MESSAGE_LEVEL_MISMATCH);
    }

    @Test
    public void execute_alreadyEnrolledAndLevelsDoNotMatch_reportsAlreadyEnrolled() {
        // a hand-edited data file can hold an enrollment between mismatched levels
        TuiTrackerBuilder builder = new TuiTrackerBuilder().withNextIds(5, 6);
        getTypicalStudents().forEach(builder::withStudent);
        getTypicalLessons().forEach(builder::withLesson);
        getTypicalEnrollments().forEach(builder::withEnrollment);
        builder.withEnrollment(new Enrollment(ALICE.getId(), ENGLISH.getId()));
        Model model = new ModelManager(builder.build(), new UserPrefs(), CLOCK);

        assertCommandFailure(enroll(ALICE.getId().value, ENGLISH.getId().value), model, MESSAGE_ALREADY_ENROLLED);
    }

    /**
     * Returns a model holding the typical data plus {@code extraStudents} and {@code extraLessons}. These tests
     * add their own records because the typical data has no student who could join a lesson of the same level
     * without already being in it.
     */
    private static Model modelWith(List<Student> extraStudents, List<Lesson> extraLessons) {
        TuiTrackerBuilder builder = new TuiTrackerBuilder().withNextIds(6, 7);
        getTypicalStudents().forEach(builder::withStudent);
        extraStudents.forEach(builder::withStudent);
        getTypicalLessons().forEach(builder::withLesson);
        extraLessons.forEach(builder::withLesson);
        getTypicalEnrollments().forEach(builder::withEnrollment);
        return new ModelManager(builder.build(), new UserPrefs(), CLOCK);
    }

    private static AddCommand enroll(int studentId, int lessonId) {
        return new AddCommand(new ExistingStudent(new StudentId(studentId)),
                new ExistingLesson(new LessonId(lessonId)));
    }
}
