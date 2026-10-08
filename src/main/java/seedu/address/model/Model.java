package seedu.address.model;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
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
 * The API of the Model component.
 *
 * <p>Commands validate with the query methods first and then call the mutating methods, which assume the input
 * has been validated. This API is shared by every command, so it only changes through a contract request.
 */
public interface Model {

    //// user prefs

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    //// persistence

    /**
     * Replaces all TuiTracker data with the data in {@code tuiTracker}.
     */
    void setTuiTracker(ReadOnlyTuiTracker tuiTracker);

    /** Returns the TuiTracker data. */
    ReadOnlyTuiTracker getTuiTracker();

    //// lookup

    /**
     * Returns the student with the given ID, if there is one.
     */
    Optional<Student> findStudent(StudentId id);

    /**
     * Returns the lesson with the given ID, if there is one.
     */
    Optional<Lesson> findLesson(LessonId id);

    //// duplicates, enrollment and clashes

    /**
     * Returns an existing student with the same name and contact, if there is one.
     */
    Optional<Student> findDuplicateStudent(Name name, Contact contact);

    /**
     * Returns an existing lesson with the same subject, level and timeslot, if there is one.
     */
    Optional<Lesson> findIdenticalLesson(Subject subject, Level level, Timeslot timeslot);

    /**
     * Returns true if the student is enrolled in the lesson.
     */
    boolean isEnrolled(StudentId studentId, LessonId lessonId);

    /**
     * Returns true if {@code timeslot} overlaps an existing lesson on the same day.
     * A lesson that starts exactly when another ends does not clash with it.
     */
    boolean hasClash(Timeslot timeslot);

    //// derived data

    /**
     * Returns the IDs of the lessons the student is enrolled in, in ascending order.
     */
    List<LessonId> getLessonIdsOf(StudentId studentId);

    /**
     * Returns the students enrolled in the lesson, in ascending order of ID.
     */
    List<Student> getStudentsIn(LessonId lessonId);

    //// payment

    /**
     * Returns the current calendar month, according to the clock the model was created with.
     */
    YearMonth currentMonth();

    /**
     * Returns true if the student has been marked as paid for the current month.
     * The student must exist.
     */
    boolean isPaid(StudentId studentId);

    /**
     * Marks the student as paid for the current month and returns the updated student.
     * The student must exist.
     */
    Student markPaid(StudentId studentId);

    //// create and delete

    /**
     * Creates a student with a new ID and returns it.
     */
    Student addStudent(Name name, Contact contact, Level level);

    /**
     * Creates a lesson with a new ID and returns it.
     */
    Lesson addLesson(Subject subject, Level level, Timeslot timeslot, Fee fee);

    /**
     * Enrolls the student in the lesson. Both must exist, and the student must not already be enrolled.
     */
    void enroll(StudentId studentId, LessonId lessonId);

    /**
     * Deletes the student and all of the student's enrollments, and returns the deleted student.
     * The student must exist. If the list is being filtered by this student, the filter is cleared.
     */
    Student deleteStudent(StudentId studentId);

    /**
     * Deletes the lesson and all enrollments in it, and returns the deleted lesson.
     * The lesson must exist. If the list is being filtered by this lesson, the filter is cleared.
     */
    Lesson deleteLesson(LessonId lessonId);

    //// filter

    /**
     * Shows only the lessons the student is enrolled in, replacing any earlier filter.
     * The filter stays up to date as enrollments change. The student must exist.
     */
    void filterLessonsByStudent(StudentId studentId);

    /**
     * Shows only the students enrolled in the lesson, replacing any earlier filter.
     * The filter stays up to date as enrollments change. The lesson must exist.
     */
    void filterStudentsByLesson(LessonId lessonId);

    /**
     * Clears any filter, so that every student and every lesson is shown.
     */
    void resetFilters();

    //// view

    /**
     * Returns an unmodifiable view of the students to show, in ascending order of ID, with any filter applied.
     */
    ObservableList<Student> getStudentList();

    /**
     * Returns an unmodifiable view of the lessons to show, ordered by day, start time and then ID,
     * with any filter applied.
     */
    ObservableList<Lesson> getLessonList();

    /**
     * Returns which list the main panel should show.
     */
    ListView getListView();

    /**
     * Sets which list the main panel should show.
     */
    void setListView(ListView listView);

    /**
     * Returns a property that changes when {@link #setListView(ListView)} is called, for the UI to observe.
     */
    ReadOnlyObjectProperty<ListView> listViewProperty();

}
