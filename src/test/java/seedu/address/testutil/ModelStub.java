package seedu.address.testutil;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyTuiTracker;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * A {@code Model} in which every method fails. Extend it and override only the methods a test needs, so a
 * change to the {@code Model} API only ever needs this one file updated.
 */
public class ModelStub implements Model {

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public GuiSettings getGuiSettings() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setTuiTracker(ReadOnlyTuiTracker tuiTracker) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ReadOnlyTuiTracker getTuiTracker() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public Optional<Student> findStudent(StudentId id) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public Optional<Lesson> findLesson(LessonId id) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public Optional<Student> findDuplicateStudent(Name name, Contact contact) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public Optional<Lesson> findIdenticalLesson(Subject subject, Level level, Timeslot timeslot) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public boolean isEnrolled(StudentId studentId, LessonId lessonId) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public boolean hasClash(Timeslot timeslot) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public List<LessonId> getLessonIdsOf(StudentId studentId) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public List<Student> getStudentsIn(LessonId lessonId) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public YearMonth currentMonth() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public boolean isPaid(StudentId studentId) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public Student markPaid(StudentId studentId) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public Student addStudent(Name name, Contact contact, Level level) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public Lesson addLesson(Subject subject, Level level, Timeslot timeslot, Fee fee) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void enroll(StudentId studentId, LessonId lessonId) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public Student deleteStudent(StudentId studentId) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public Lesson deleteLesson(LessonId lessonId) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void filterLessonsByStudent(StudentId studentId) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void filterStudentsByLesson(LessonId lessonId) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void resetFilters() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ObservableList<Student> getStudentList() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ObservableList<Lesson> getLessonList() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ListView getListView() {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public void setListView(ListView listView) {
        throw new AssertionError("This method should not be called.");
    }

    @Override
    public ReadOnlyObjectProperty<ListView> listViewProperty() {
        throw new AssertionError("This method should not be called.");
    }

}
