package seedu.address.model.util;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.YearMonth;

import seedu.address.model.ReadOnlyTuiTracker;
import seedu.address.model.TuiTracker;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.Student;

/**
 * Contains utility methods for populating {@code TuiTracker} with sample data.
 */
public class SampleDataUtil {

    /**
     * Returns a TuiTracker with three students, four lessons and their enrollments.
     * Every student is enrolled only in lessons of their own level, and Bryan has paid for the current month.
     */
    public static ReadOnlyTuiTracker getSampleTuiTracker() {
        TuiTracker sample = new TuiTracker();

        Student alice = sample.addStudent(new Name("Alice Tan"), new Contact("91234567"), new Level("Primary 6"));
        Student bryan = sample.addStudent(new Name("Bryan Lim"), new Contact("98765432"), new Level("Secondary 2"));
        Student chloe = sample.addStudent(new Name("Chloe Ng"), new Contact("81234567"), new Level("Primary 4"));

        Lesson mathematics = sample.addLesson(new Subject("Mathematics"), new Level("Primary 6"),
                new Timeslot(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)), new Fee("50.00"));
        Lesson science = sample.addLesson(new Subject("Science"), new Level("Primary 6"),
                new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(17, 0), LocalTime.of(18, 0)), new Fee("45.00"));
        Lesson secondaryScience = sample.addLesson(new Subject("Science"), new Level("Secondary 2"),
                new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(16, 0), LocalTime.of(17, 30)), new Fee("60.00"));
        Lesson english = sample.addLesson(new Subject("English"), new Level("Primary 4"),
                new Timeslot(DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(11, 0)), new Fee("40.00"));

        sample.enroll(alice.getId(), mathematics.getId());
        sample.enroll(alice.getId(), science.getId());
        sample.enroll(bryan.getId(), secondaryScience.getId());
        sample.enroll(chloe.getId(), english.getId());

        sample.markPaid(bryan.getId(), YearMonth.now());
        return sample;
    }

}
