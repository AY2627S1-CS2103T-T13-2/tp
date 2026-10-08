package seedu.address.testutil;

import seedu.address.model.TuiTracker;
import seedu.address.model.enrollment.Enrollment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/**
 * A utility class that builds a {@code TuiTracker} holding the typical students, lessons and enrollments.
 */
public class TypicalTuiTracker {

    private TypicalTuiTracker() {} // prevents instantiation

    /**
     * Returns a {@code TuiTracker} with the typical students, lessons and enrollments. The next student ID is 5
     * and the next lesson ID is 6, so {@code DANIEL} and {@code HISTORY} can be added without clashing.
     */
    public static TuiTracker getTypicalTuiTracker() {
        TuiTrackerBuilder builder = new TuiTrackerBuilder().withNextIds(5, 6);
        for (Student student : TypicalStudents.getTypicalStudents()) {
            builder.withStudent(student);
        }
        for (Lesson lesson : TypicalLessons.getTypicalLessons()) {
            builder.withLesson(lesson);
        }
        for (Enrollment enrollment : TypicalEnrollments.getTypicalEnrollments()) {
            builder.withEnrollment(enrollment);
        }
        return builder.build();
    }
}
