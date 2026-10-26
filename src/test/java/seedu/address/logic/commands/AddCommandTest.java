package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;
import static seedu.address.logic.commands.AddCommand.MESSAGE_ALREADY_ENROLLED;
import static seedu.address.logic.commands.AddCommand.MESSAGE_DUPLICATE_LESSON;
import static seedu.address.logic.commands.AddCommand.MESSAGE_DUPLICATE_STUDENT;
import static seedu.address.logic.commands.AddCommand.MESSAGE_LESSON_CLASH;
import static seedu.address.logic.commands.AddCommand.MESSAGE_LEVEL_MISMATCH;
import static seedu.address.logic.commands.AddCommand.MESSAGE_NOTE_SAME_NAME;
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
import static seedu.address.testutil.TypicalStudents.CHLOE;
import static seedu.address.testutil.TypicalStudents.DANIEL;
import static seedu.address.testutil.TypicalStudents.getTypicalStudents;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

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
import seedu.address.model.TuiTracker;
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

    /** A student who is not in the typical data: Daniel Koh, Primary 6. */
    private static final NewStudent DANIEL_DETAILS =
            new NewStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());

    /** A Primary 6 lesson that is not in the typical data and does not clash with it: History on Friday. */
    private static final NewLesson HISTORY_DETAILS =
            new NewLesson(HISTORY.getSubject(), HISTORY.getLevel(), HISTORY.getTimeslot(), HISTORY.getFee());

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

    //// defensive checks

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AddCommand command = new AddCommand(EXISTING_STUDENT, EXISTING_LESSON);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_modelThatDoesNotEnroll_failsPostConditionAssertion() {
        // Assertions are on in Gradle test runs; skip, and do not fail, where they have been turned off.
        assumeTrue(assertionsAreEnabled());
        // A model that breaks the Model contract: enroll does nothing, so the student is not enrolled afterwards.
        Model brokenModel = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK) {
            @Override
            public void enroll(StudentId studentId, LessonId lessonId) {
            }
        };
        AddCommand command = new AddCommand(new ExistingStudent(ALICE.getId()), HISTORY_DETAILS);

        assertThrows(AssertionError.class, () -> command.execute(brokenModel));
    }

    private static boolean assertionsAreEnabled() {
        boolean enabled = false;
        assert enabled = true; // deliberate side effect: only runs when assertions are on
        return enabled;
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

    //// creating a new student

    @Test
    public void execute_newStudentAndExistingLesson_createsAndEnrollsTheStudent() {
        Model model = typicalModel();
        Model expectedModel = typicalModel();
        Student created = expectedModel.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
        expectedModel.enroll(created.getId(), MATHEMATICS.getId());

        assertCommandSuccess(new AddCommand(DANIEL_DETAILS, new ExistingLesson(MATHEMATICS.getId())), model,
                "Enrolled Daniel Koh [Student ID: 5] in Mathematics [Lesson ID: 1]. Payment status: Unpaid.",
                expectedModel);
        assertEquals(6, model.getTuiTracker().getNextStudentId());
    }

    @Test
    public void execute_newStudentWithTheSameNameButAnotherContact_isNotADuplicate() {
        NewStudent sameNameOtherContact =
                new NewStudent(ALICE.getName(), new Contact("90000000"), ALICE.getLevel());
        Model model = typicalModel();
        Model expectedModel = typicalModel();
        Student created = expectedModel.addStudent(ALICE.getName(), new Contact("90000000"), ALICE.getLevel());
        expectedModel.enroll(created.getId(), MATHEMATICS.getId());

        assertCommandSuccess(new AddCommand(sameNameOtherContact, new ExistingLesson(MATHEMATICS.getId())), model,
                "Enrolled Alice Tan [Student ID: 5] in Mathematics [Lesson ID: 1]. Payment status: Unpaid.\n"
                        + String.format(MESSAGE_NOTE_SAME_NAME, ALICE.getName(), ALICE.getId()),
                expectedModel);
    }

    //// creating a new lesson

    @Test
    public void execute_existingStudentAndNewLesson_createsAndEnrollsTheLesson() {
        Model model = typicalModel();
        Model expectedModel = typicalModel();
        Lesson created = expectedModel.addLesson(HISTORY.getSubject(), HISTORY.getLevel(), HISTORY.getTimeslot(),
                HISTORY.getFee());
        expectedModel.enroll(ALICE.getId(), created.getId());

        assertCommandSuccess(new AddCommand(new ExistingStudent(ALICE.getId()), HISTORY_DETAILS), model,
                "Enrolled Alice Tan [Student ID: 1] in History [Lesson ID: 6]. Payment status: Unpaid.",
                expectedModel);
        assertEquals(7, model.getTuiTracker().getNextLessonId());
    }

    @Test
    public void execute_paidStudentAndNewLesson_reportsPaid() {
        NewLesson physics = newLesson("Physics", "Secondary 2", DayOfWeek.FRIDAY, 14, 0, 15, 0);
        Model model = typicalModel();
        Model expectedModel = typicalModel();
        Lesson created = expectedModel.addLesson(physics.subject(), physics.level(), physics.timeslot(),
                physics.fee());
        expectedModel.enroll(BRYAN.getId(), created.getId());

        assertCommandSuccess(new AddCommand(new ExistingStudent(BRYAN.getId()), physics), model,
                "Enrolled Bryan Lim [Student ID: 2] in Physics [Lesson ID: 6]. Payment status: Paid.",
                expectedModel);
    }

    //// creating a new student and a new lesson

    @Test
    public void execute_newStudentAndNewLesson_createsBothAndEnrollsTheStudent() {
        Model model = typicalModel();
        Model expectedModel = typicalModel();
        Student student = expectedModel.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
        Lesson lesson = expectedModel.addLesson(HISTORY.getSubject(), HISTORY.getLevel(), HISTORY.getTimeslot(),
                HISTORY.getFee());
        expectedModel.enroll(student.getId(), lesson.getId());

        assertCommandSuccess(new AddCommand(DANIEL_DETAILS, HISTORY_DETAILS), model,
                "Enrolled Daniel Koh [Student ID: 5] in History [Lesson ID: 6]. Payment status: Unpaid.",
                expectedModel);
    }

    @Test
    public void execute_newStudentAndNewLesson_numbersThemSeparately() {
        Model model = new ModelManager(new TuiTracker(), new UserPrefs(), CLOCK);
        Model expectedModel = new ModelManager(new TuiTracker(), new UserPrefs(), CLOCK);
        Student student = expectedModel.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
        Lesson lesson = expectedModel.addLesson(HISTORY.getSubject(), HISTORY.getLevel(), HISTORY.getTimeslot(),
                HISTORY.getFee());
        expectedModel.enroll(student.getId(), lesson.getId());

        // both are the first of their kind, so both are ID 1
        assertCommandSuccess(new AddCommand(DANIEL_DETAILS, HISTORY_DETAILS), model,
                "Enrolled Daniel Koh [Student ID: 1] in History [Lesson ID: 1]. Payment status: Unpaid.",
                expectedModel);
    }

    //// lesson clashes

    @Test
    public void execute_newLessonThatOverlapsAnotherLesson_throwsCommandException() {
        // MATHEMATICS is on Monday from 15:00 to 16:30
        NewLesson overlapping = newLesson("History", "Primary 6", DayOfWeek.MONDAY, 16, 0, 17, 0);

        assertCommandFailure(new AddCommand(new ExistingStudent(ALICE.getId()), overlapping), typicalModel(),
                MESSAGE_LESSON_CLASH);
    }

    @Test
    public void execute_newLessonThatContainsOrIsInsideAnotherLesson_throwsCommandException() {
        assertCommandFailure(new AddCommand(new ExistingStudent(ALICE.getId()),
                newLesson("History", "Primary 6", DayOfWeek.MONDAY, 14, 0, 18, 0)), typicalModel(),
                MESSAGE_LESSON_CLASH);
        assertCommandFailure(new AddCommand(new ExistingStudent(ALICE.getId()),
                newLesson("History", "Primary 6", DayOfWeek.MONDAY, 15, 30, 16, 0)), typicalModel(),
                MESSAGE_LESSON_CLASH);
    }

    @Test
    public void execute_newLessonThatStartsWhenAnotherEnds_isAllowed() {
        NewLesson backToBack = newLesson("History", "Primary 6", DayOfWeek.MONDAY, 16, 30, 17, 30);
        Model model = typicalModel();
        Model expectedModel = typicalModel();
        Lesson created = expectedModel.addLesson(backToBack.subject(), backToBack.level(), backToBack.timeslot(),
                backToBack.fee());
        expectedModel.enroll(ALICE.getId(), created.getId());

        assertCommandSuccess(new AddCommand(new ExistingStudent(ALICE.getId()), backToBack), model,
                "Enrolled Alice Tan [Student ID: 1] in History [Lesson ID: 6]. Payment status: Unpaid.",
                expectedModel);
    }

    @Test
    public void execute_newLessonThatEndsWhenAnotherStarts_isAllowed() {
        NewLesson backToBack = newLesson("History", "Primary 6", DayOfWeek.MONDAY, 14, 0, 15, 0);
        Model model = typicalModel();
        Model expectedModel = typicalModel();
        Lesson created = expectedModel.addLesson(backToBack.subject(), backToBack.level(), backToBack.timeslot(),
                backToBack.fee());
        expectedModel.enroll(ALICE.getId(), created.getId());

        assertCommandSuccess(new AddCommand(new ExistingStudent(ALICE.getId()), backToBack), model,
                "Enrolled Alice Tan [Student ID: 1] in History [Lesson ID: 6]. Payment status: Unpaid.",
                expectedModel);
    }

    @Test
    public void execute_newLessonAtTheSameHoursOnAnotherDay_isAllowed() {
        NewLesson tuesday = newLesson("History", "Primary 6", DayOfWeek.TUESDAY, 15, 0, 16, 30);
        Model model = typicalModel();
        Model expectedModel = typicalModel();
        Lesson created = expectedModel.addLesson(tuesday.subject(), tuesday.level(), tuesday.timeslot(),
                tuesday.fee());
        expectedModel.enroll(ALICE.getId(), created.getId());

        assertCommandSuccess(new AddCommand(new ExistingStudent(ALICE.getId()), tuesday), model,
                "Enrolled Alice Tan [Student ID: 1] in History [Lesson ID: 6]. Payment status: Unpaid.",
                expectedModel);
    }

    @Test
    public void execute_newLessonClashesWithAnyLesson_notOnlyTheStudentsOwn() {
        // CHLOE is only in ENGLISH on Saturday, but there is one tutor, so Alice's Monday lesson still blocks it
        NewLesson primaryFour = newLesson("Art", "Primary 4", DayOfWeek.MONDAY, 15, 30, 16, 0);

        assertCommandFailure(new AddCommand(new ExistingStudent(CHLOE.getId()), primaryFour), typicalModel(),
                MESSAGE_LESSON_CLASH);
    }

    //// duplicates

    @Test
    public void execute_duplicateStudent_throwsCommandException() {
        // the same name and contact as ALICE, even though the level is different and would match ENGLISH
        NewStudent duplicate = new NewStudent(ALICE.getName(), ALICE.getContact(), ENGLISH.getLevel());

        assertCommandFailure(new AddCommand(duplicate, new ExistingLesson(ENGLISH.getId())), typicalModel(),
                String.format(MESSAGE_DUPLICATE_STUDENT, ALICE.getId()));
    }

    @Test
    public void execute_duplicateLesson_throwsCommandException() {
        // the same subject, level and timeslot as MATHEMATICS, with a different fee
        NewLesson duplicate = new NewLesson(MATHEMATICS.getSubject(), MATHEMATICS.getLevel(),
                MATHEMATICS.getTimeslot(), new Fee("99.00"));

        assertCommandFailure(new AddCommand(new ExistingStudent(ALICE.getId()), duplicate), typicalModel(),
                String.format(MESSAGE_DUPLICATE_LESSON, MATHEMATICS.getId()));
    }

    @Test
    public void execute_duplicateLessonWithTheLevelSpelledDifferently_throwsCommandException() {
        NewLesson duplicate = new NewLesson(MATHEMATICS.getSubject(), new Level("primary    6"),
                MATHEMATICS.getTimeslot(), MATHEMATICS.getFee());

        assertCommandFailure(new AddCommand(new ExistingStudent(ALICE.getId()), duplicate), typicalModel(),
                String.format(MESSAGE_DUPLICATE_LESSON, MATHEMATICS.getId()));
    }

    @Test
    public void execute_sameSubjectAndTimeslotButAnotherLevel_isAClashNotADuplicate() {
        NewStudent primaryFive = new NewStudent(DANIEL.getName(), DANIEL.getContact(), new Level("Primary 5"));
        NewLesson otherLevel = new NewLesson(MATHEMATICS.getSubject(), new Level("Primary 5"),
                MATHEMATICS.getTimeslot(), MATHEMATICS.getFee());

        assertCommandFailure(new AddCommand(primaryFive, otherLevel), typicalModel(), MESSAGE_LESSON_CLASH);
    }

    //// the order of the checks

    @Test
    public void execute_missingLessonIsReportedBeforeADuplicateStudent() {
        NewStudent duplicate = new NewStudent(ALICE.getName(), ALICE.getContact(), ALICE.getLevel());

        assertCommandFailure(new AddCommand(duplicate, new ExistingLesson(new LessonId(99))), typicalModel(),
                String.format(MESSAGE_LESSON_NOT_FOUND, 99));
    }

    @Test
    public void execute_missingStudentIsReportedBeforeADuplicateLesson() {
        NewLesson duplicate = new NewLesson(MATHEMATICS.getSubject(), MATHEMATICS.getLevel(),
                MATHEMATICS.getTimeslot(), MATHEMATICS.getFee());

        assertCommandFailure(new AddCommand(new ExistingStudent(new StudentId(99)), duplicate), typicalModel(),
                String.format(MESSAGE_STUDENT_NOT_FOUND, 99));
    }

    @Test
    public void execute_duplicateStudentIsReportedBeforeADuplicateLesson() {
        NewStudent duplicateStudent = new NewStudent(ALICE.getName(), ALICE.getContact(), ALICE.getLevel());
        NewLesson duplicateLesson = new NewLesson(MATHEMATICS.getSubject(), MATHEMATICS.getLevel(),
                MATHEMATICS.getTimeslot(), MATHEMATICS.getFee());

        assertCommandFailure(new AddCommand(duplicateStudent, duplicateLesson), typicalModel(),
                String.format(MESSAGE_DUPLICATE_STUDENT, ALICE.getId()));
    }

    @Test
    public void execute_duplicateIsReportedBeforeALevelMismatch() {
        // a duplicate of ALICE whose level, Primary 4, also does not match MATHEMATICS, which is Primary 6
        NewStudent duplicate = new NewStudent(ALICE.getName(), ALICE.getContact(), ENGLISH.getLevel());

        assertCommandFailure(new AddCommand(duplicate, new ExistingLesson(MATHEMATICS.getId())), typicalModel(),
                String.format(MESSAGE_DUPLICATE_STUDENT, ALICE.getId()));
    }

    @Test
    public void execute_levelMismatchIsReportedBeforeAClash() {
        // a Primary 4 lesson at the same time as MATHEMATICS, for a Primary 6 student
        NewLesson wrongLevelAndClashing = new NewLesson(new Subject("Art"), new Level("Primary 4"),
                MATHEMATICS.getTimeslot(), new Fee("30"));

        assertCommandFailure(new AddCommand(new ExistingStudent(ALICE.getId()), wrongLevelAndClashing),
                typicalModel(), MESSAGE_LEVEL_MISMATCH);
    }

    //// a failed command creates nothing

    @Test
    public void execute_newStudentAndClashingNewLesson_createsNeitherAndUsesNoId() {
        // assertCommandFailure compares the whole data, including the ID counters
        NewLesson clashing = newLesson("History", "Primary 6", DayOfWeek.MONDAY, 16, 0, 17, 0);

        assertCommandFailure(new AddCommand(DANIEL_DETAILS, clashing), typicalModel(), MESSAGE_LESSON_CLASH);
    }

    @Test
    public void execute_newStudentAndNewLessonOfDifferentLevels_createsNeitherAndUsesNoId() {
        NewLesson primaryFour = newLesson("Art", "Primary 4", DayOfWeek.TUESDAY, 10, 0, 11, 0);

        assertCommandFailure(new AddCommand(DANIEL_DETAILS, primaryFour), typicalModel(), MESSAGE_LEVEL_MISMATCH);
    }

    @Test
    public void execute_newStudentAndMissingLesson_createsNothing() {
        assertCommandFailure(new AddCommand(DANIEL_DETAILS, new ExistingLesson(new LessonId(99))), typicalModel(),
                String.format(MESSAGE_LESSON_NOT_FOUND, 99));
    }

    @Test
    public void execute_newStudentAndExistingLessonOfAnotherLevel_createsNoStudent() {
        // a Primary 4 student cannot join MATHEMATICS, which is Primary 6
        NewStudent primaryFour = new NewStudent(DANIEL.getName(), DANIEL.getContact(), new Level("Primary 4"));

        assertCommandFailure(new AddCommand(primaryFour, new ExistingLesson(MATHEMATICS.getId())), typicalModel(),
                MESSAGE_LEVEL_MISMATCH);
    }

    @Test
    public void execute_newLessonAndMismatchedExistingStudent_createsNoLesson() {
        // ALICE is Primary 6; the new lesson is Primary 4
        NewLesson primaryFour = newLesson("Art", "Primary 4", DayOfWeek.TUESDAY, 10, 0, 11, 0);

        assertCommandFailure(new AddCommand(new ExistingStudent(ALICE.getId()), primaryFour), typicalModel(),
                MESSAGE_LEVEL_MISMATCH);
    }

    private static Model typicalModel() {
        return new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
    }

    private static NewLesson newLesson(String subject, String level, DayOfWeek day, int startHour, int startMinute,
            int endHour, int endMinute) {
        return new NewLesson(new Subject(subject), new Level(level),
                new Timeslot(day, LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute)),
                new Fee("40.00"));
    }

    //// near-duplicates and notes

    @Test
    public void execute_newStudentSameNameDifferentCase_rejectedAsDuplicate() {
        NewStudent lowerCase = new NewStudent(new Name("alice tan"), ALICE.getContact(), ALICE.getLevel());
        assertCommandFailure(new AddCommand(lowerCase, new ExistingLesson(MATHEMATICS.getId())), typicalModel(),
                String.format(MESSAGE_DUPLICATE_STUDENT, ALICE.getId()));
    }

    @Test
    public void execute_newLessonSubjectDifferentCase_rejectedAsIdentical() {
        NewLesson lowerCase = new NewLesson(new Subject("mathematics"), MATHEMATICS.getLevel(),
                MATHEMATICS.getTimeslot(), MATHEMATICS.getFee());
        assertCommandFailure(new AddCommand(new ExistingStudent(ALICE.getId()), lowerCase), typicalModel(),
                String.format(MESSAGE_DUPLICATE_LESSON, MATHEMATICS.getId()));
    }

    @Test
    public void execute_newStudentSameNameDifferentContact_enrollsWithNote() throws Exception {
        NewStudent namesake = new NewStudent(ALICE.getName(), new Contact("99990000"), ALICE.getLevel());
        String feedback = new AddCommand(namesake, new ExistingLesson(MATHEMATICS.getId()))
                .execute(typicalModel()).getFeedbackToUser();
        assertTrue(feedback.endsWith("\n" + String.format(MESSAGE_NOTE_SAME_NAME, ALICE.getName(), ALICE.getId())));
    }
}
