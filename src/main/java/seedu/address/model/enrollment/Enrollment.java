package seedu.address.model.enrollment;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

/**
 * Represents the link between one Student and one Lesson that the student attends.
 * It refers to both by ID, so deleting a student or lesson must also delete its enrollments.
 * Guarantees: immutable.
 */
public class Enrollment {

    private final StudentId studentId;
    private final LessonId lessonId;

    /**
     * Creates an enrollment. Both IDs must be present and not null.
     */
    public Enrollment(StudentId studentId, LessonId lessonId) {
        requireAllNonNull(studentId, lessonId);
        this.studentId = studentId;
        this.lessonId = lessonId;
    }

    public StudentId getStudentId() {
        return studentId;
    }

    public LessonId getLessonId() {
        return lessonId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Enrollment otherEnrollment)) {
            return false;
        }

        return studentId.equals(otherEnrollment.studentId) && lessonId.equals(otherEnrollment.lessonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, lessonId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("studentId", studentId)
                .add("lessonId", lessonId)
                .toString();
    }

}
