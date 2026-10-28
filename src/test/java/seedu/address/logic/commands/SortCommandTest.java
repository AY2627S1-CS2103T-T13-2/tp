package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalClock.CLOCK;
import static seedu.address.testutil.TypicalLessons.ENGLISH;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.SCIENCE_P6;
import static seedu.address.testutil.TypicalLessons.SCIENCE_S2;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalStudents.CHLOE;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.SortField;
import seedu.address.model.UserPrefs;

public class SortCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
    }

    @Test
    public void execute_studentNameAscending_ordersStudentsAlphabetically() {
        SortCommand command = new SortCommand("student", "name", SortField.STUDENT_NAME, true);
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "students", "name", "ascending"),
                result.getFeedbackToUser());
        assertEquals(ListView.STUDENTS, model.getListView());
        assertEquals(Arrays.asList(ALICE, BRYAN, CHLOE), model.getStudentList());
    }

    @Test
    public void execute_studentNameDescending_ordersStudentsReverseAlphabetically() {
        SortCommand command = new SortCommand("student", "name", SortField.STUDENT_NAME, false);
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "students", "name", "descending"),
                result.getFeedbackToUser());
        assertEquals(ListView.STUDENTS, model.getListView());
        assertEquals(Arrays.asList(CHLOE, BRYAN, ALICE), model.getStudentList());
    }

    @Test
    public void execute_studentPaymentAscending_placesUnpaidStudentsAtTop() {
        SortCommand command = new SortCommand("student", "payment", SortField.STUDENT_PAYMENT, true);
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "students", "payment", "ascending"),
                result.getFeedbackToUser());
        assertEquals(ListView.STUDENTS, model.getListView());
        // ALICE and CHLOE are unpaid, BRYAN is paid in the current month
        assertEquals(Arrays.asList(ALICE, CHLOE, BRYAN), model.getStudentList());
    }

    @Test
    public void execute_studentPaymentDescending_placesPaidStudentsAtTop() {
        SortCommand command = new SortCommand("student", "payment", SortField.STUDENT_PAYMENT, false);
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "students", "payment", "descending"),
                result.getFeedbackToUser());
        assertEquals(ListView.STUDENTS, model.getListView());
        assertEquals(Arrays.asList(BRYAN, CHLOE, ALICE), model.getStudentList());
    }

    @Test
    public void execute_studentIdAscending_ordersStudentsById() {
        SortCommand command = new SortCommand("student", "id", SortField.STUDENT_ID, true);
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "students", "id", "ascending"),
                result.getFeedbackToUser());
        assertEquals(Arrays.asList(ALICE, BRYAN, CHLOE), model.getStudentList());
    }

    @Test
    public void execute_studentLevelAscending_ordersStudentsByLevel() {
        SortCommand command = new SortCommand("student", "level", SortField.STUDENT_LEVEL, true);
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "students", "level", "ascending"),
                result.getFeedbackToUser());
        // Primary 4 (CHLOE), Primary 6 (ALICE), Secondary 2 (BRYAN)
        assertEquals(Arrays.asList(CHLOE, ALICE, BRYAN), model.getStudentList());
    }

    @Test
    public void execute_lessonIdAscending_ordersLessonsById() {
        SortCommand command = new SortCommand("lesson", "id", SortField.LESSON_ID, true);
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "lessons", "id", "ascending"),
                result.getFeedbackToUser());
        assertEquals(ListView.LESSONS, model.getListView());
        assertEquals(Arrays.asList(MATHEMATICS, SCIENCE_P6, SCIENCE_S2, ENGLISH), model.getLessonList());
    }

    @Test
    public void execute_lessonSubjectAscending_ordersLessonsBySubject() {
        SortCommand command = new SortCommand("lesson", "subject", SortField.LESSON_SUBJECT, true);
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "lessons", "subject", "ascending"),
                result.getFeedbackToUser());
        assertEquals(ListView.LESSONS, model.getListView());
        assertEquals(Arrays.asList(ENGLISH, MATHEMATICS, SCIENCE_P6, SCIENCE_S2), model.getLessonList());
    }

    @Test
    public void execute_lessonFeeDescending_ordersLessonsByHighestFee() {
        SortCommand command = new SortCommand("lesson", "fee", SortField.LESSON_FEE, false);
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "lessons", "fee", "descending"),
                result.getFeedbackToUser());
        assertEquals(Arrays.asList(SCIENCE_S2, MATHEMATICS, SCIENCE_P6, ENGLISH), model.getLessonList());
    }

    @Test
    public void execute_lessonTimeAscending_ordersLessonsChronologically() {
        SortCommand command = new SortCommand("lesson", "time", SortField.LESSON_TIME, true);
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "lessons", "time", "ascending"),
                result.getFeedbackToUser());
        assertEquals(Arrays.asList(MATHEMATICS, SCIENCE_S2, SCIENCE_P6, ENGLISH), model.getLessonList());
    }

    @Test
    public void execute_lessonLevelAscending_ordersLessonsByLevel() {
        SortCommand command = new SortCommand("lesson", "level", SortField.LESSON_LEVEL, true);
        CommandResult result = command.execute(model);

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "lessons", "level", "ascending"),
                result.getFeedbackToUser());
        // Primary 4 (ENGLISH), Primary 6 (MATHEMATICS, SCIENCE_P6), Secondary 2 (SCIENCE_S2)
        assertEquals(Arrays.asList(ENGLISH, MATHEMATICS, SCIENCE_P6, SCIENCE_S2), model.getLessonList());
    }

    @Test
    public void isDataMutating_returnsFalse() {
        SortCommand command = new SortCommand("student", "name", SortField.STUDENT_NAME, true);
        assertFalse(command.isDataMutating());
    }

    @Test
    public void equals_and_hashCode() {
        SortCommand sortStudentNameAsc = new SortCommand("student", "name", SortField.STUDENT_NAME, true);
        SortCommand sortStudentNameAscCopy = new SortCommand("student", "name", SortField.STUDENT_NAME, true);
        SortCommand sortStudentNameDesc = new SortCommand("student", "name", SortField.STUDENT_NAME, false);
        SortCommand sortStudentPayment = new SortCommand("student", "payment", SortField.STUDENT_PAYMENT, true);
        SortCommand sortLessonSubject = new SortCommand("lesson", "subject", SortField.LESSON_SUBJECT, true);

        // same object -> returns true
        assertTrue(sortStudentNameAsc.equals(sortStudentNameAsc));

        // same values -> returns true
        assertTrue(sortStudentNameAsc.equals(sortStudentNameAscCopy));
        assertEquals(sortStudentNameAsc.hashCode(), sortStudentNameAscCopy.hashCode());

        // different types -> returns false
        assertFalse(sortStudentNameAsc.equals(1));

        // null -> returns false
        assertFalse(sortStudentNameAsc.equals(null));

        // different direction -> returns false
        assertFalse(sortStudentNameAsc.equals(sortStudentNameDesc));
        assertNotEquals(sortStudentNameAsc.hashCode(), sortStudentNameDesc.hashCode());

        // different field -> returns false
        assertFalse(sortStudentNameAsc.equals(sortStudentPayment));

        // different entity -> returns false
        assertFalse(sortStudentNameAsc.equals(sortLessonSubject));
    }
}
