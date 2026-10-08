package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalClock.CLOCK;
import static seedu.address.testutil.TypicalClock.CURRENT_MONTH;
import static seedu.address.testutil.TypicalLessons.ENGLISH;
import static seedu.address.testutil.TypicalLessons.HISTORY;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.SCIENCE_P6;
import static seedu.address.testutil.TypicalLessons.SCIENCE_S2;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalStudents.CHLOE;
import static seedu.address.testutil.TypicalStudents.DANIEL;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

public class ModelManagerTest {

    private final ModelManager modelManager = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);

    //// construction and user prefs

    @Test
    public void constructor_startsEmptyOnTheStudentList() {
        ModelManager empty = new ModelManager();

        assertEquals(new UserPrefs(), empty.getUserPrefs());
        assertEquals(new GuiSettings(), empty.getGuiSettings());
        assertEquals(new TuiTracker(), new TuiTracker(empty.getTuiTracker()));
        assertEquals(ListView.STUDENTS, empty.getListView());
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ModelManager(null, new UserPrefs(), CLOCK));
        assertThrows(NullPointerException.class, () -> new ModelManager(new TuiTracker(), null, CLOCK));
        assertThrows(NullPointerException.class, () -> new ModelManager(new TuiTracker(), new UserPrefs(), null));
    }

    @Test
    public void constructor_copiesTheGivenData() {
        TuiTracker source = getTypicalTuiTracker();
        ModelManager model = new ModelManager(source, new UserPrefs(), CLOCK);

        source.deleteStudent(ALICE.getId());

        assertTrue(model.findStudent(ALICE.getId()).isPresent());
    }

    @Test
    public void setGuiSettings() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));

        GuiSettings guiSettings = new GuiSettings(1200, 700, 30, 50);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setTuiTracker_replacesTheData() {
        ModelManager model = new ModelManager();

        model.setTuiTracker(getTypicalTuiTracker());

        assertEquals(getTypicalTuiTracker(), new TuiTracker(model.getTuiTracker()));
        assertEquals(Arrays.asList(ALICE, BRYAN, CHLOE), model.getStudentList());
    }

    //// lookups delegate to the data

    @Test
    public void lookups() {
        assertEquals(Optional.of(ALICE), modelManager.findStudent(ALICE.getId()));
        assertEquals(Optional.empty(), modelManager.findStudent(DANIEL.getId()));
        assertEquals(Optional.of(MATHEMATICS), modelManager.findLesson(MATHEMATICS.getId()));
        assertEquals(Optional.of(ALICE), modelManager.findDuplicateStudent(ALICE.getName(), ALICE.getContact()));
        assertEquals(Optional.of(MATHEMATICS), modelManager.findIdenticalLesson(MATHEMATICS.getSubject(),
                MATHEMATICS.getLevel(), MATHEMATICS.getTimeslot()));
        assertTrue(modelManager.isEnrolled(ALICE.getId(), MATHEMATICS.getId()));
        assertFalse(modelManager.isEnrolled(ALICE.getId(), ENGLISH.getId()));
        assertTrue(modelManager.hasClash(MATHEMATICS.getTimeslot()));
        assertFalse(modelManager.hasClash(HISTORY.getTimeslot()));
        assertEquals(Arrays.asList(MATHEMATICS.getId(), SCIENCE_P6.getId()),
                modelManager.getLessonIdsOf(ALICE.getId()));
        assertEquals(Collections.singletonList(ALICE), modelManager.getStudentsIn(MATHEMATICS.getId()));
    }

    //// payment

    @Test
    public void currentMonth_comesFromTheClock() {
        assertEquals(CURRENT_MONTH, modelManager.currentMonth());
    }

    @Test
    public void markPaid_andIsPaid() {
        assertFalse(modelManager.isPaid(ALICE.getId()));

        Student paid = modelManager.markPaid(ALICE.getId());

        assertTrue(paid.isPaid(CURRENT_MONTH));
        assertTrue(modelManager.isPaid(ALICE.getId()));
        assertTrue(modelManager.isPaid(BRYAN.getId())); // BRYAN was already paid this month
        assertFalse(modelManager.isPaid(DANIEL.getId())); // no such student
    }

    @Test
    public void isPaid_resetsByItselfWhenANewMonthStarts() {
        modelManager.markPaid(ALICE.getId());
        Clock nextMonth = Clock.fixed(Instant.parse("2026-11-01T00:30:00Z"), ZoneOffset.UTC);

        ModelManager laterModel = new ModelManager(modelManager.getTuiTracker(), new UserPrefs(), nextMonth);

        assertFalse(laterModel.isPaid(ALICE.getId()));
        assertFalse(laterModel.isPaid(BRYAN.getId()));
    }

    //// create and delete

    @Test
    public void addStudent_andAddLesson_appearInTheLists() {
        Student student = modelManager.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
        Lesson lesson = modelManager.addLesson(HISTORY.getSubject(), HISTORY.getLevel(), HISTORY.getTimeslot(),
                HISTORY.getFee());

        assertEquals(student, modelManager.getStudentList().get(3));
        assertTrue(modelManager.getLessonList().contains(lesson));
    }

    @Test
    public void enroll_andDelete_updateTheDerivedData() {
        Student student = modelManager.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
        modelManager.enroll(student.getId(), MATHEMATICS.getId());
        assertEquals(Arrays.asList(ALICE, student), modelManager.getStudentsIn(MATHEMATICS.getId()));

        modelManager.deleteLesson(MATHEMATICS.getId());

        assertEquals(Collections.emptyList(), modelManager.getLessonIdsOf(student.getId()));
        assertEquals(student, modelManager.deleteStudent(student.getId()));
    }

    //// ordering of the lists

    @Test
    public void getStudentList_isInAscendingOrderOfId() {
        modelManager.setTuiTracker(new seedu.address.testutil.TuiTrackerBuilder()
                .withStudent(CHLOE).withStudent(ALICE).withStudent(BRYAN).build());

        assertEquals(Arrays.asList(ALICE, BRYAN, CHLOE), modelManager.getStudentList());
    }

    @Test
    public void getLessonList_isOrderedByDayThenStartTimeThenId() {
        // MATHEMATICS is on Monday, SCIENCE_S2 on Wednesday, SCIENCE_P6 on Thursday and ENGLISH on Saturday
        assertEquals(Arrays.asList(MATHEMATICS, SCIENCE_S2, SCIENCE_P6, ENGLISH), modelManager.getLessonList());
    }

    @Test
    public void getLessonList_lessonsOnTheSameDayAreOrderedByStartTime() {
        Lesson later = new seedu.address.testutil.LessonBuilder(HISTORY).withId(7)
                .withTimeslot(java.time.DayOfWeek.MONDAY, java.time.LocalTime.of(18, 0), java.time.LocalTime.of(19, 0))
                .build();
        Lesson earlier = new seedu.address.testutil.LessonBuilder(HISTORY).withId(8)
                .withTimeslot(java.time.DayOfWeek.MONDAY, java.time.LocalTime.of(9, 0), java.time.LocalTime.of(10, 0))
                .build();
        modelManager.setTuiTracker(new seedu.address.testutil.TuiTrackerBuilder()
                .withLesson(later).withLesson(earlier).withLesson(MATHEMATICS).build());

        assertEquals(Arrays.asList(earlier, MATHEMATICS, later), modelManager.getLessonList());
    }

    @Test
    public void getLists_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getStudentList().remove(0));
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getLessonList().remove(0));
    }

    //// filters

    @Test
    public void filterLessonsByStudent_showsOnlyThatStudentsLessons() {
        modelManager.filterLessonsByStudent(ALICE.getId());

        assertEquals(Arrays.asList(MATHEMATICS, SCIENCE_P6), modelManager.getLessonList());
        assertEquals(Arrays.asList(ALICE, BRYAN, CHLOE), modelManager.getStudentList()); // students unaffected
    }

    @Test
    public void filterStudentsByLesson_showsOnlyThatLessonsStudents() {
        modelManager.filterStudentsByLesson(SCIENCE_S2.getId());

        assertEquals(Collections.singletonList(BRYAN), modelManager.getStudentList());
        assertEquals(4, modelManager.getLessonList().size()); // lessons unaffected
    }

    @Test
    public void filter_withNoResults_showsAnEmptyList() {
        modelManager.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
        modelManager.filterLessonsByStudent(new seedu.address.model.student.StudentId(5)); // DANIEL's real ID

        assertEquals(Collections.emptyList(), modelManager.getLessonList());
    }

    @Test
    public void filter_newFilterReplacesTheOldOne() {
        modelManager.filterLessonsByStudent(ALICE.getId());

        modelManager.filterStudentsByLesson(MATHEMATICS.getId());

        assertEquals(4, modelManager.getLessonList().size()); // the student filter is gone
        assertEquals(Collections.singletonList(ALICE), modelManager.getStudentList());
    }

    @Test
    public void resetFilters_showsEverything() {
        modelManager.filterLessonsByStudent(ALICE.getId());

        modelManager.resetFilters();

        assertEquals(4, modelManager.getLessonList().size());
        assertEquals(3, modelManager.getStudentList().size());
    }

    @Test
    public void filter_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.filterLessonsByStudent(null));
        assertThrows(NullPointerException.class, () -> modelManager.filterStudentsByLesson(null));
    }

    @Test
    public void filter_staysCorrectWhenAStudentIsEnrolled() {
        modelManager.filterStudentsByLesson(MATHEMATICS.getId());
        assertEquals(Collections.singletonList(ALICE), modelManager.getStudentList());

        Student daniel = modelManager.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
        modelManager.enroll(daniel.getId(), MATHEMATICS.getId());

        assertEquals(Arrays.asList(ALICE, daniel), modelManager.getStudentList());
    }

    @Test
    public void filter_staysCorrectWhenALessonIsEnrolled() {
        modelManager.filterLessonsByStudent(ALICE.getId());
        Lesson history = modelManager.addLesson(HISTORY.getSubject(), HISTORY.getLevel(), HISTORY.getTimeslot(),
                HISTORY.getFee());
        assertEquals(Arrays.asList(MATHEMATICS, SCIENCE_P6), modelManager.getLessonList()); // not enrolled yet

        modelManager.enroll(ALICE.getId(), history.getId());

        assertEquals(Arrays.asList(MATHEMATICS, SCIENCE_P6, history), modelManager.getLessonList()); // Mon, Thu, Fri
    }

    @Test
    public void filter_staysCorrectWhenAnotherLessonIsDeleted() {
        modelManager.filterLessonsByStudent(ALICE.getId());

        modelManager.deleteLesson(SCIENCE_P6.getId());

        assertEquals(Collections.singletonList(MATHEMATICS), modelManager.getLessonList());
    }

    @Test
    public void deleteStudent_whoseLessonsAreShown_clearsTheFilter() {
        modelManager.filterLessonsByStudent(ALICE.getId());

        modelManager.deleteStudent(ALICE.getId());

        assertEquals(4, modelManager.getLessonList().size());
    }

    @Test
    public void deleteLesson_whoseStudentsAreShown_clearsTheFilter() {
        modelManager.filterStudentsByLesson(MATHEMATICS.getId());

        modelManager.deleteLesson(MATHEMATICS.getId());

        assertEquals(3, modelManager.getStudentList().size());
    }

    @Test
    public void deleteStudent_notTheFilteredOne_keepsTheFilter() {
        modelManager.filterLessonsByStudent(ALICE.getId());

        modelManager.deleteStudent(BRYAN.getId());

        assertEquals(Arrays.asList(MATHEMATICS, SCIENCE_P6), modelManager.getLessonList());
    }

    @Test
    public void setTuiTracker_keepsTheFilterUpToDate() {
        modelManager.filterStudentsByLesson(MATHEMATICS.getId());
        TuiTracker withoutEnrollments = new seedu.address.testutil.TuiTrackerBuilder()
                .withStudent(ALICE).withLesson(MATHEMATICS).build();

        modelManager.setTuiTracker(withoutEnrollments);

        assertEquals(Collections.emptyList(), modelManager.getStudentList());
    }

    //// view

    @Test
    public void setListView_changesTheViewAndNotifiesObservers() {
        List<ListView> seen = new ArrayList<>();
        modelManager.listViewProperty().addListener((observable, oldView, newView) -> seen.add(newView));

        modelManager.setListView(ListView.LESSONS);
        modelManager.setListView(ListView.STUDENTS);

        assertEquals(ListView.STUDENTS, modelManager.getListView());
        assertEquals(Arrays.asList(ListView.LESSONS, ListView.STUDENTS), seen);
        assertThrows(NullPointerException.class, () -> modelManager.setListView(null));
    }

    //// equals

    @Test
    public void equals() {
        TuiTracker tracker = getTypicalTuiTracker();
        UserPrefs userPrefs = new UserPrefs();
        ModelManager modelManager = new ModelManager(tracker, userPrefs, CLOCK);
        ModelManager modelManagerCopy = new ModelManager(tracker, userPrefs, CLOCK);

        assertTrue(modelManager.equals(modelManagerCopy));
        assertTrue(modelManager.equals(modelManager));
        assertFalse(modelManager.equals(null));
        assertFalse(modelManager.equals(5));
        assertEquals(modelManager.hashCode(), modelManagerCopy.hashCode());

        // different data
        assertFalse(modelManager.equals(new ModelManager(new TuiTracker(), userPrefs, CLOCK)));

        // different filter
        modelManagerCopy.filterLessonsByStudent(ALICE.getId());
        assertFalse(modelManager.equals(modelManagerCopy));
        modelManagerCopy.resetFilters();
        assertTrue(modelManager.equals(modelManagerCopy));

        // different view
        modelManagerCopy.setListView(ListView.LESSONS);
        assertFalse(modelManager.equals(modelManagerCopy));

        // different user prefs
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(tracker, differentUserPrefs, CLOCK)));
    }
}
