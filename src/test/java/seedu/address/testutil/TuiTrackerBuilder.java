package seedu.address.testutil;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.ReadOnlyTuiTracker;
import seedu.address.model.TuiTracker;
import seedu.address.model.enrollment.Enrollment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/**
 * A utility class to help with building TuiTracker objects. Unlike {@code TuiTracker}, it keeps the IDs of the
 * records it is given, instead of generating new ones.
 *
 * <p>Example usage: <br>
 *     {@code TuiTracker tracker = new TuiTrackerBuilder().withStudent(ALICE).withLesson(MATHEMATICS).build();}
 */
public class TuiTrackerBuilder {

    private final List<Student> students = new ArrayList<>();
    private final List<Lesson> lessons = new ArrayList<>();
    private final List<Enrollment> enrollments = new ArrayList<>();
    private int nextStudentId = 1;
    private int nextLessonId = 1;

    /**
     * Adds {@code student} to the TuiTracker that we are building.
     */
    public TuiTrackerBuilder withStudent(Student student) {
        students.add(student);
        return this;
    }

    /**
     * Adds {@code lesson} to the TuiTracker that we are building.
     */
    public TuiTrackerBuilder withLesson(Lesson lesson) {
        lessons.add(lesson);
        return this;
    }

    /**
     * Adds {@code enrollment} to the TuiTracker that we are building.
     */
    public TuiTrackerBuilder withEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        return this;
    }

    /**
     * Sets the IDs that the next new student and the next new lesson will get.
     */
    public TuiTrackerBuilder withNextIds(int nextStudentId, int nextLessonId) {
        this.nextStudentId = nextStudentId;
        this.nextLessonId = nextLessonId;
        return this;
    }

    /**
     * Builds the TuiTracker. The ID counters are never lower than the highest ID in use.
     */
    public TuiTracker build() {
        return new TuiTracker(new ReadOnlyTuiTracker() {
            @Override
            public ObservableList<Student> getStudentList() {
                return FXCollections.observableArrayList(students);
            }

            @Override
            public ObservableList<Lesson> getLessonList() {
                return FXCollections.observableArrayList(lessons);
            }

            @Override
            public List<Enrollment> getEnrollmentList() {
                return enrollments;
            }

            @Override
            public int getNextStudentId() {
                return nextStudentId;
            }

            @Override
            public int getNextLessonId() {
                return nextLessonId;
            }
        });
    }

}
