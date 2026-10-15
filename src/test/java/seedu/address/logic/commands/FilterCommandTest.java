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

import org.junit.jupiter.api.Test;

import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.LessonId;
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

        // same object -> returns true
        assertTrue(filterByAlice.equals(filterByAlice));

        // same values -> returns true
        assertTrue(filterByAlice.equals(new FilterCommand(ALICE.getId())));

        // different student -> returns false
        assertFalse(filterByAlice.equals(filterByBryan));

        // student vs lesson -> returns false
        assertFalse(filterByAlice.equals(filterByMaths));

        // null -> returns false
        assertFalse(filterByAlice.equals(null));

        // different type -> returns false
        assertFalse(filterByAlice.equals(5));
    }
}
