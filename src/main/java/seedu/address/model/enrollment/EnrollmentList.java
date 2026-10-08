package seedu.address.model.enrollment;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

/**
 * The set of enrollments, i.e. which students attend which lessons. A student can be enrolled in a lesson only once.
 * This list does not check that the students and lessons exist; {@code TuiTracker} does.
 */
public class EnrollmentList {

    public static final String MESSAGE_DUPLICATE_ENROLLMENT = "A student can be enrolled in a lesson only once.";

    private final List<Enrollment> internalList = new ArrayList<>();

    /**
     * Returns true if the student is enrolled in the lesson.
     */
    public boolean contains(StudentId studentId, LessonId lessonId) {
        return internalList.contains(new Enrollment(studentId, lessonId));
    }

    /**
     * Adds an enrollment.
     *
     * @throws IllegalArgumentException if the student is already enrolled in the lesson.
     */
    public void add(Enrollment toAdd) {
        requireNonNull(toAdd);
        if (internalList.contains(toAdd)) {
            throw new IllegalArgumentException(MESSAGE_DUPLICATE_ENROLLMENT);
        }
        internalList.add(toAdd);
    }

    /**
     * Removes every enrollment of the given student.
     */
    public void removeAllOfStudent(StudentId studentId) {
        requireNonNull(studentId);
        internalList.removeIf(enrollment -> enrollment.getStudentId().equals(studentId));
    }

    /**
     * Removes every enrollment in the given lesson.
     */
    public void removeAllOfLesson(LessonId lessonId) {
        requireNonNull(lessonId);
        internalList.removeIf(enrollment -> enrollment.getLessonId().equals(lessonId));
    }

    /**
     * Returns the IDs of the lessons the student is enrolled in, in ascending order.
     */
    public List<LessonId> lessonIdsOf(StudentId studentId) {
        requireNonNull(studentId);
        return internalList.stream()
                .filter(enrollment -> enrollment.getStudentId().equals(studentId))
                .map(Enrollment::getLessonId)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Returns the IDs of the students enrolled in the lesson, in ascending order.
     */
    public List<StudentId> studentIdsIn(LessonId lessonId) {
        requireNonNull(lessonId);
        return internalList.stream()
                .filter(enrollment -> enrollment.getLessonId().equals(lessonId))
                .map(Enrollment::getStudentId)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Replaces the contents of this list with {@code enrollments}.
     *
     * @throws IllegalArgumentException if {@code enrollments} contains the same enrollment twice.
     */
    public void setEnrollments(List<Enrollment> enrollments) {
        requireNonNull(enrollments);
        Set<Enrollment> seen = new HashSet<>();
        for (Enrollment enrollment : enrollments) {
            if (!seen.add(enrollment)) {
                throw new IllegalArgumentException(MESSAGE_DUPLICATE_ENROLLMENT);
            }
        }
        List<Enrollment> sorted = new ArrayList<>(enrollments);
        sorted.sort(Comparator.comparing(Enrollment::getStudentId).thenComparing(Enrollment::getLessonId));
        internalList.clear();
        internalList.addAll(sorted);
    }

    /**
     * Returns the enrollments as an unmodifiable list, ordered by student ID and then lesson ID.
     */
    public List<Enrollment> asUnmodifiableList() {
        List<Enrollment> sorted = new ArrayList<>(internalList);
        sorted.sort(Comparator.comparing(Enrollment::getStudentId).thenComparing(Enrollment::getLessonId));
        return Collections.unmodifiableList(sorted);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EnrollmentList otherList)) {
            return false;
        }

        return new HashSet<>(internalList).equals(new HashSet<>(otherList.internalList));
    }

    @Override
    public int hashCode() {
        return new HashSet<>(internalList).hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

}
