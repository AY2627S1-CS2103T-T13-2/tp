package seedu.address.model;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.model.enrollment.Enrollment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/**
 * Unmodifiable view of all TuiTracker data.
 */
public interface ReadOnlyTuiTracker {

    /**
     * Returns an unmodifiable view of the students, in ascending order of ID.
     */
    ObservableList<Student> getStudentList();

    /**
     * Returns an unmodifiable view of the lessons, in ascending order of ID.
     */
    ObservableList<Lesson> getLessonList();

    /**
     * Returns an unmodifiable view of the enrollments, ordered by student ID and then lesson ID.
     */
    List<Enrollment> getEnrollmentList();

    /**
     * Returns the ID the next new student will get, or {@link IdGenerator#ALL_IDS_USED} if every ID has been used.
     */
    long getNextStudentId();

    /**
     * Returns the ID the next new lesson will get, or {@link IdGenerator#ALL_IDS_USED} if every ID has been used.
     */
    long getNextLessonId();

    /**
     * Returns true if a new student can still get an ID.
     */
    default boolean hasStudentIdLeft() {
        return getNextStudentId() < IdGenerator.ALL_IDS_USED;
    }

    /**
     * Returns true if a new lesson can still get an ID.
     */
    default boolean hasLessonIdLeft() {
        return getNextLessonId() < IdGenerator.ALL_IDS_USED;
    }

}
