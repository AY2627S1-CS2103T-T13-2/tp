package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalClock.CLOCK;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import org.junit.jupiter.api.Test;

import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.TuiTracker;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.TuiTrackerBuilder;

public class ListCommandTest {

    private final Model model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);

    @Test
    public void constructor_nullView_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListCommand(null));
    }

    @Test
    public void constructor_scheduleView_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ListCommand(ListView.SCHEDULE));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListCommand(ListView.STUDENTS).execute(null));
    }

    @Test
    public void execute_students_clearsFilterAndShowsAllStudents() {
        model.filterStudentsByLesson(MATHEMATICS.getId());
        model.setListView(ListView.LESSONS);
        Model expectedModel = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        String expectedMessage = String.format(ListCommand.MESSAGE_LISTED_STUDENTS, 3);

        assertCommandSuccess(new ListCommand(ListView.STUDENTS), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_lessons_clearsFilterAndShowsAllLessons() {
        model.filterLessonsByStudent(ALICE.getId());
        Model expectedModel = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        expectedModel.setListView(ListView.LESSONS);
        String expectedMessage = String.format(ListCommand.MESSAGE_LISTED_LESSONS, 4);

        assertCommandSuccess(new ListCommand(ListView.LESSONS), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_studentsWithNoRecords_showsEmptyMessage() {
        Model emptyModel = new ModelManager(new TuiTracker(), new UserPrefs(), CLOCK);
        Model expectedModel = new ModelManager(new TuiTracker(), new UserPrefs(), CLOCK);

        assertCommandSuccess(new ListCommand(ListView.STUDENTS), emptyModel,
                ListCommand.MESSAGE_NO_STUDENTS, expectedModel);
    }

    @Test
    public void execute_lessonsWithNoRecords_showsEmptyMessage() {
        Model emptyModel = new ModelManager(new TuiTracker(), new UserPrefs(), CLOCK);
        Model expectedModel = new ModelManager(new TuiTracker(), new UserPrefs(), CLOCK);
        expectedModel.setListView(ListView.LESSONS);

        assertCommandSuccess(new ListCommand(ListView.LESSONS), emptyModel,
                ListCommand.MESSAGE_NO_LESSONS, expectedModel);
    }

    @Test
    public void execute_studentsWithOneRecord_usesSingularMessage() {
        TuiTracker tracker = new TuiTrackerBuilder().withStudent(ALICE).build();
        Model oneStudentModel = new ModelManager(tracker, new UserPrefs(), CLOCK);
        Model expectedModel = new ModelManager(tracker, new UserPrefs(), CLOCK);

        assertCommandSuccess(new ListCommand(ListView.STUDENTS), oneStudentModel,
                ListCommand.MESSAGE_LISTED_ONE_STUDENT, expectedModel);
    }

    @Test
    public void execute_lessonsWithOneRecord_usesSingularMessage() {
        TuiTracker tracker = new TuiTrackerBuilder().withLesson(MATHEMATICS).build();
        Model oneLessonModel = new ModelManager(tracker, new UserPrefs(), CLOCK);
        Model expectedModel = new ModelManager(tracker, new UserPrefs(), CLOCK);
        expectedModel.setListView(ListView.LESSONS);

        assertCommandSuccess(new ListCommand(ListView.LESSONS), oneLessonModel,
                ListCommand.MESSAGE_LISTED_ONE_LESSON, expectedModel);
    }

    @Test
    public void isDataMutating_returnsFalse() {
        assertFalse(new ListCommand(ListView.STUDENTS).isDataMutating());
        assertFalse(new ListCommand(ListView.LESSONS).isDataMutating());
    }

    @Test
    public void equals() {
        ListCommand listStudents = new ListCommand(ListView.STUDENTS);
        ListCommand listStudentsCopy = new ListCommand(ListView.STUDENTS);
        ListCommand listLessons = new ListCommand(ListView.LESSONS);

        assertTrue(listStudents.equals(listStudentsCopy));
        assertTrue(listStudents.equals(listStudents));
        assertFalse(listStudents.equals(null));
        assertFalse(listStudents.equals(5));
        assertFalse(listStudents.equals(listLessons));
        assertEquals(listStudents.hashCode(), listStudentsCopy.hashCode());
    }
}
