package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalClock.CLOCK;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.SCIENCE_S2;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import java.time.DayOfWeek;

import org.junit.jupiter.api.Test;

import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.StudentId;

public class FilterCommandTest {

    private Model model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);

    @Test
    public void execute_studentExists_showsThatStudentsLessons() {
        // ALICE (ID 1) is enrolled in MATHEMATICS and SCIENCE_P6; filtering by her shows only those lessons
        FilterCommand filterCommand = new FilterCommand(ALICE.getId());

        Model expectedModel = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        expectedModel.filterLessonsByStudent(ALICE.getId());
        expectedModel.setListView(ListView.LESSONS);

        String expectedMessage = String.format(FilterCommand.MESSAGE_SHOWING_LESSONS,
                expectedModel.getLessonList().size(), ALICE.getId());

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_lessonExists_showsThatLessonsStudents() {
        // SCIENCE_S2 (ID 3) has only BRYAN enrolled
        FilterCommand filterCommand = new FilterCommand(SCIENCE_S2.getId());

        Model expectedModel = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        expectedModel.filterStudentsByLesson(SCIENCE_S2.getId());
        expectedModel.setListView(ListView.STUDENTS);

        String expectedMessage = String.format(FilterCommand.MESSAGE_SHOWING_STUDENTS,
                expectedModel.getStudentList().size(), SCIENCE_S2.getId());

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_dayGiven_showsLessonsOnThatDay() {
        FilterCommand filterCommand = new FilterCommand(DayOfWeek.MONDAY);

        Model expectedModel = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        expectedModel.filterLessonsByDay(DayOfWeek.MONDAY);
        expectedModel.setListView(ListView.LESSONS);

        String expectedMessage = String.format(FilterCommand.MESSAGE_SHOWING_LESSONS_ON_DAY,
                expectedModel.getLessonList().size(), Timeslot.dayName(DayOfWeek.MONDAY));

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_subjectGiven_showsLessonsWithThatSubject() {
        Subject science = new Subject("science");
        FilterCommand filterCommand = new FilterCommand(science, null);

        Model expectedModel = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        expectedModel.filterLessonsBySubjectAndLevel(science, null);
        expectedModel.setListView(ListView.LESSONS);

        String expectedMessage = String.format(FilterCommand.MESSAGE_SHOWING_LESSONS_MATCHING, 2,
                "subject science");

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_levelGiven_showsLessonsAtThatLevel() {
        Level primary6 = new Level("Primary 6");
        FilterCommand filterCommand = new FilterCommand(null, primary6);

        Model expectedModel = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        expectedModel.filterLessonsBySubjectAndLevel(null, primary6);
        expectedModel.setListView(ListView.LESSONS);

        String expectedMessage = String.format(FilterCommand.MESSAGE_SHOWING_LESSONS_MATCHING, 2,
                "level Primary 6");

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_subjectAndLevelGiven_showsLessonsMatchingBoth() {
        Subject science = new Subject("Science");
        Level primary6 = new Level("Primary 6");
        FilterCommand filterCommand = new FilterCommand(science, primary6);

        Model expectedModel = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        expectedModel.filterLessonsBySubjectAndLevel(science, primary6);
        expectedModel.setListView(ListView.LESSONS);

        String expectedMessage = String.format(FilterCommand.MESSAGE_SHOWING_LESSONS_MATCHING, 1,
                "subject Science and level Primary 6");

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_paidGiven_showsPaidStudents() {
        FilterCommand filterCommand = new FilterCommand(true);

        Model expectedModel = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        expectedModel.filterStudentsByPaidStatus(true);
        expectedModel.setListView(ListView.STUDENTS);

        String expectedMessage = String.format(FilterCommand.MESSAGE_SHOWING_STUDENTS_BY_PAID_STATUS,
                expectedModel.getStudentList().size(), "paid");

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unpaidGiven_showsUnpaidStudents() {
        FilterCommand filterCommand = new FilterCommand(false);

        Model expectedModel = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        expectedModel.filterStudentsByPaidStatus(false);
        expectedModel.setListView(ListView.STUDENTS);

        String expectedMessage = String.format(FilterCommand.MESSAGE_SHOWING_STUDENTS_BY_PAID_STATUS,
                expectedModel.getStudentList().size(), "unpaid");

        assertCommandSuccess(filterCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_studentIdNotFound_throwsCommandException() {
        StudentId missingId = new StudentId(999);
        FilterCommand filterCommand = new FilterCommand(missingId);

        assertCommandFailure(filterCommand, model, String.format(MESSAGE_STUDENT_NOT_FOUND, missingId));
    }

    @Test
    public void execute_lessonIdNotFound_throwsCommandException() {
        LessonId missingId = new LessonId(999);
        FilterCommand filterCommand = new FilterCommand(missingId);

        assertCommandFailure(filterCommand, model, String.format(MESSAGE_LESSON_NOT_FOUND, missingId));
    }

    @Test
    public void equals() {
        FilterCommand filterByAlice = new FilterCommand(ALICE.getId());
        FilterCommand filterByBryan = new FilterCommand(BRYAN.getId());
        FilterCommand filterByMaths = new FilterCommand(MATHEMATICS.getId());
        FilterCommand filterByMonday = new FilterCommand(DayOfWeek.MONDAY);
        FilterCommand filterByTuesday = new FilterCommand(DayOfWeek.TUESDAY);
        FilterCommand filterByScience = new FilterCommand(new Subject("Science"), null);
        FilterCommand filterByPrimary6 = new FilterCommand(null, new Level("Primary 6"));
        FilterCommand filterByScienceP6 = new FilterCommand(new Subject("Science"), new Level("Primary 6"));
        FilterCommand filterByPaid = new FilterCommand(true);
        FilterCommand filterByUnpaid = new FilterCommand(false);

        // same object -> returns true
        assertTrue(filterByAlice.equals(filterByAlice));

        // same values -> returns true
        assertTrue(filterByAlice.equals(new FilterCommand(ALICE.getId())));
        assertTrue(filterByMonday.equals(new FilterCommand(DayOfWeek.MONDAY)));

        // different student -> returns false
        assertFalse(filterByAlice.equals(filterByBryan));

        // student vs lesson -> returns false
        assertFalse(filterByAlice.equals(filterByMaths));

        // different day -> returns false
        assertFalse(filterByMonday.equals(filterByTuesday));

        // lesson vs day -> returns false
        assertFalse(filterByMaths.equals(filterByMonday));

        // same subject and level -> returns true
        assertTrue(filterByScienceP6.equals(new FilterCommand(new Subject("Science"), new Level("Primary 6"))));

        // subject only vs level only vs both -> returns false
        assertFalse(filterByScience.equals(filterByPrimary6));
        assertFalse(filterByScience.equals(filterByScienceP6));
        assertFalse(filterByPrimary6.equals(filterByScienceP6));

        // same paid status -> returns true
        assertTrue(filterByPaid.equals(new FilterCommand(true)));

        // different paid status -> returns false
        assertFalse(filterByPaid.equals(filterByUnpaid));

        // paid vs student -> returns false
        assertFalse(filterByPaid.equals(filterByAlice));

        // null -> returns false
        assertFalse(filterByAlice.equals(null));

        // different type -> returns false
        assertFalse(filterByAlice.equals(5));
    }
}
