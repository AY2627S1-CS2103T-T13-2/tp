package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalClock.CURRENT_MONTH;
import static seedu.address.testutil.TypicalEnrollments.ALICE_MATHEMATICS;
import static seedu.address.testutil.TypicalEnrollments.ALICE_SCIENCE_P6;
import static seedu.address.testutil.TypicalEnrollments.BRYAN_SCIENCE_S2;
import static seedu.address.testutil.TypicalEnrollments.CHLOE_ENGLISH;
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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.enrollment.Enrollment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.lesson.exceptions.LessonNotFoundException;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.exceptions.StudentNotFoundException;
import seedu.address.testutil.LessonBuilder;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TuiTrackerBuilder;

public class TuiTrackerTest {

    private final TuiTracker tracker = getTypicalTuiTracker();

    @Test
    public void constructor_isEmpty() {
        TuiTracker empty = new TuiTracker();

        assertEquals(Collections.emptyList(), empty.getStudentList());
        assertEquals(Collections.emptyList(), empty.getLessonList());
        assertEquals(Collections.emptyList(), empty.getEnrollmentList());
        assertEquals(1, empty.getNextStudentId());
        assertEquals(1, empty.getNextLessonId());
    }

    @Test
    public void typicalTracker_holdsTheTypicalData() {
        assertEquals(Arrays.asList(ALICE, BRYAN, CHLOE), tracker.getStudentList());
        assertEquals(Arrays.asList(MATHEMATICS, SCIENCE_P6, SCIENCE_S2, ENGLISH), tracker.getLessonList());
        assertEquals(Arrays.asList(ALICE_MATHEMATICS, ALICE_SCIENCE_P6, BRYAN_SCIENCE_S2, CHLOE_ENGLISH),
                tracker.getEnrollmentList());
    }

    //// resetData

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> tracker.resetData(null));
    }

    @Test
    public void resetData_replacesEverything() {
        TuiTracker target = new TuiTracker();
        target.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());

        target.resetData(tracker);

        assertEquals(tracker, target);
    }

    @Test
    public void resetData_withSelf_keepsTheData() {
        TuiTracker expected = new TuiTracker(tracker);

        tracker.resetData(tracker);

        assertEquals(expected, tracker);
    }

    @Test
    public void constructor_twoStudentsWithTheSameId_throwsIllegalArgumentException() {
        Student sameIdAsAlice = new StudentBuilder(BRYAN).withId(ALICE.getId().value).build();
        TuiTrackerBuilder builder = new TuiTrackerBuilder().withStudent(ALICE).withStudent(sameIdAsAlice);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void constructor_twoLessonsWithTheSameId_throwsIllegalArgumentException() {
        Lesson sameIdAsMathematics = new LessonBuilder(ENGLISH).withId(MATHEMATICS.getId().value).build();
        TuiTrackerBuilder builder = new TuiTrackerBuilder().withLesson(MATHEMATICS).withLesson(sameIdAsMathematics);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void constructor_enrollmentOfAMissingStudent_throwsIllegalArgumentException() {
        TuiTrackerBuilder builder = new TuiTrackerBuilder().withLesson(MATHEMATICS).withEnrollment(ALICE_MATHEMATICS);

        assertThrows(IllegalArgumentException.class, TuiTracker.MESSAGE_DANGLING_ENROLLMENT, builder::build);
    }

    @Test
    public void constructor_enrollmentOfAMissingLesson_throwsIllegalArgumentException() {
        TuiTrackerBuilder builder = new TuiTrackerBuilder().withStudent(ALICE).withEnrollment(ALICE_MATHEMATICS);

        assertThrows(IllegalArgumentException.class, TuiTracker.MESSAGE_DANGLING_ENROLLMENT, builder::build);
    }

    @Test
    public void constructor_sameEnrollmentTwice_throwsIllegalArgumentException() {
        TuiTrackerBuilder builder = new TuiTrackerBuilder().withStudent(ALICE).withLesson(MATHEMATICS)
                .withEnrollment(ALICE_MATHEMATICS).withEnrollment(ALICE_MATHEMATICS);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void constructor_counterBelowTheHighestId_isRaised() {
        TuiTracker built = new TuiTrackerBuilder().withStudent(CHLOE).withLesson(ENGLISH).withNextIds(1, 1).build();

        assertEquals(4, built.getNextStudentId()); // CHLOE has ID 3
        assertEquals(5, built.getNextLessonId()); // ENGLISH has ID 4
    }

    @Test
    public void constructor_copyIsIndependentOfTheOriginal() {
        TuiTracker copy = new TuiTracker(tracker);

        copy.deleteStudent(ALICE.getId());

        assertTrue(tracker.findStudent(ALICE.getId()).isPresent());
        assertNotEquals(tracker, copy);
    }

    //// queries

    @Test
    public void findStudent_andFindLesson() {
        assertEquals(Optional.of(ALICE), tracker.findStudent(ALICE.getId()));
        assertEquals(Optional.empty(), tracker.findStudent(DANIEL.getId()));
        assertEquals(Optional.of(MATHEMATICS), tracker.findLesson(MATHEMATICS.getId()));
        assertEquals(Optional.empty(), tracker.findLesson(HISTORY.getId()));
    }

    @Test
    public void findDuplicateStudent_matchesNameAndContact() {
        assertEquals(Optional.of(ALICE), tracker.findDuplicateStudent(ALICE.getName(), ALICE.getContact()));

        // the same name with another contact is a different student
        assertEquals(Optional.empty(), tracker.findDuplicateStudent(ALICE.getName(), DANIEL.getContact()));
        // the same contact with another name is a different student
        assertEquals(Optional.empty(), tracker.findDuplicateStudent(DANIEL.getName(), ALICE.getContact()));
        assertThrows(NullPointerException.class, () -> tracker.findDuplicateStudent(null, ALICE.getContact()));
    }

    @Test
    public void findDuplicateStudent_withSeveralMatches_returnsTheLowestId() {
        Student sameAsAlice = new StudentBuilder(ALICE).withId(9).build();
        TuiTracker withDuplicate = new TuiTrackerBuilder().withStudent(sameAsAlice).withStudent(ALICE).build();

        assertEquals(Optional.of(ALICE), withDuplicate.findDuplicateStudent(ALICE.getName(), ALICE.getContact()));
    }

    @Test
    public void findIdenticalLesson_matchesSubjectLevelAndTimeslot() {
        assertEquals(Optional.of(MATHEMATICS), tracker.findIdenticalLesson(MATHEMATICS.getSubject(),
                MATHEMATICS.getLevel(), MATHEMATICS.getTimeslot()));

        // a different fee does not matter, but a different timeslot does
        assertEquals(Optional.empty(), tracker.findIdenticalLesson(MATHEMATICS.getSubject(),
                MATHEMATICS.getLevel(), HISTORY.getTimeslot()));
        assertEquals(Optional.empty(), tracker.findIdenticalLesson(HISTORY.getSubject(),
                MATHEMATICS.getLevel(), MATHEMATICS.getTimeslot()));
    }

    @Test
    public void isEnrolled() {
        assertTrue(tracker.isEnrolled(ALICE.getId(), MATHEMATICS.getId()));
        assertFalse(tracker.isEnrolled(ALICE.getId(), ENGLISH.getId()));
        assertFalse(tracker.isEnrolled(DANIEL.getId(), MATHEMATICS.getId()));
    }

    @Test
    public void hasClash() {
        // overlaps MATHEMATICS (Monday 15:00-16:30)
        assertTrue(tracker.hasClash(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0))));
        // the same timeslot
        assertTrue(tracker.hasClash(MATHEMATICS.getTimeslot()));

        // starts exactly when MATHEMATICS ends, and ends exactly when it starts
        assertFalse(tracker.hasClash(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(16, 30), LocalTime.of(17, 30))));
        assertFalse(tracker.hasClash(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 0))));

        // the same hours on another day, and a free day
        assertFalse(tracker.hasClash(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(15, 0), LocalTime.of(16, 30))));
        assertFalse(tracker.hasClash(HISTORY.getTimeslot()));
    }

    @Test
    public void getLessonIdsOf_andGetStudentsIn() {
        assertEquals(Arrays.asList(MATHEMATICS.getId(), SCIENCE_P6.getId()), tracker.getLessonIdsOf(ALICE.getId()));
        assertEquals(Collections.emptyList(), tracker.getLessonIdsOf(DANIEL.getId()));
        assertEquals(Collections.singletonList(ALICE), tracker.getStudentsIn(MATHEMATICS.getId()));
        assertEquals(Collections.emptyList(), tracker.getStudentsIn(HISTORY.getId()));
    }

    //// creating

    @Test
    public void addStudent_generatesTheNextId() {
        Student added = tracker.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());

        // DANIEL has ID 4, but the typical tracker's next student ID is 5
        assertEquals(new StudentBuilder(DANIEL).withId(5).build(), added);
        assertEquals(new StudentId(5), added.getId());
        assertEquals(Optional.of(added), tracker.findStudent(added.getId()));
        assertEquals(6, tracker.getNextStudentId());
    }

    @Test
    public void addStudent_newStudentHasNotPaid() {
        Student added = tracker.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());

        assertFalse(added.isPaid(CURRENT_MONTH));
        assertEquals(Optional.empty(), added.getLastPaidMonth());
    }

    @Test
    public void addLesson_generatesTheNextId_fromItsOwnCounter() {
        Lesson added = tracker.addLesson(HISTORY.getSubject(), HISTORY.getLevel(), HISTORY.getTimeslot(),
                HISTORY.getFee());

        assertEquals(new LessonId(6), added.getId());
        assertEquals(7, tracker.getNextLessonId());
        assertEquals(5, tracker.getNextStudentId()); // the student counter is unaffected
    }

    @Test
    public void ids_areNeverReusedAfterDeletion() {
        Student first = tracker.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
        tracker.deleteStudent(first.getId());

        Student second = tracker.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());

        assertNotEquals(first.getId(), second.getId());
        assertEquals(new StudentId(6), second.getId());
    }

    @Test
    public void enroll_newEnrollment_isRecorded() {
        Student daniel = tracker.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());

        tracker.enroll(daniel.getId(), MATHEMATICS.getId());

        assertTrue(tracker.isEnrolled(daniel.getId(), MATHEMATICS.getId()));
        assertEquals(Arrays.asList(ALICE, daniel), tracker.getStudentsIn(MATHEMATICS.getId()));
    }

    @Test
    public void enroll_alreadyEnrolled_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tracker.enroll(ALICE.getId(), MATHEMATICS.getId()));
    }

    @Test
    public void enroll_missingStudentOrLesson_throwsNotFoundException() {
        assertThrows(StudentNotFoundException.class, () -> tracker.enroll(DANIEL.getId(), MATHEMATICS.getId()));
        assertThrows(LessonNotFoundException.class, () -> tracker.enroll(ALICE.getId(), HISTORY.getId()));
    }

    //// deleting

    @Test
    public void deleteStudent_removesTheStudentAndTheirEnrollments() {
        Student deleted = tracker.deleteStudent(ALICE.getId());

        assertEquals(ALICE, deleted);
        assertEquals(Optional.empty(), tracker.findStudent(ALICE.getId()));
        assertEquals(Arrays.asList(BRYAN_SCIENCE_S2, CHLOE_ENGLISH), tracker.getEnrollmentList());
        assertEquals(Optional.of(MATHEMATICS), tracker.findLesson(MATHEMATICS.getId())); // lessons stay
    }

    @Test
    public void deleteLesson_removesTheLessonAndUnEnrollsItsStudents() {
        Lesson deleted = tracker.deleteLesson(MATHEMATICS.getId());

        assertEquals(MATHEMATICS, deleted);
        assertEquals(Optional.empty(), tracker.findLesson(MATHEMATICS.getId()));
        assertEquals(Arrays.asList(ALICE_SCIENCE_P6, BRYAN_SCIENCE_S2, CHLOE_ENGLISH), tracker.getEnrollmentList());
        assertEquals(Optional.of(ALICE), tracker.findStudent(ALICE.getId())); // students stay
        assertEquals(Collections.singletonList(SCIENCE_P6.getId()), tracker.getLessonIdsOf(ALICE.getId()));
    }

    @Test
    public void delete_missingRecord_throwsNotFoundException() {
        assertThrows(StudentNotFoundException.class, () -> tracker.deleteStudent(DANIEL.getId()));
        assertThrows(LessonNotFoundException.class, () -> tracker.deleteLesson(HISTORY.getId()));
    }

    @Test
    public void studentAndLessonWithTheSameId_areDeletedIndependently() {
        // IDs are numbered separately, so student 1 and lesson 1 both exist
        assertEquals(ALICE.getId().value, MATHEMATICS.getId().value);

        tracker.deleteStudent(ALICE.getId());

        assertTrue(tracker.findLesson(MATHEMATICS.getId()).isPresent());
    }

    //// payment

    @Test
    public void markPaid_returnsAndStoresThePaidStudent() {
        Student paid = tracker.markPaid(ALICE.getId(), CURRENT_MONTH);

        assertTrue(paid.isPaid(CURRENT_MONTH));
        assertEquals(Optional.of(paid), tracker.findStudent(ALICE.getId()));
        assertEquals(paid, tracker.getStudentList().get(0)); // still in the same place in the list
    }

    @Test
    public void markPaid_missingStudent_throwsStudentNotFoundException() {
        assertThrows(StudentNotFoundException.class, () -> tracker.markPaid(DANIEL.getId(), CURRENT_MONTH));
    }

    //// util

    @Test
    public void getLists_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> tracker.getStudentList().remove(0));
        assertThrows(UnsupportedOperationException.class, () -> tracker.getLessonList().remove(0));
        assertThrows(UnsupportedOperationException.class, () -> tracker.getEnrollmentList().clear());
    }

    @Test
    public void equals_andHashCode() {
        TuiTracker same = getTypicalTuiTracker();

        assertTrue(tracker.equals(same));
        assertTrue(tracker.equals(tracker));
        assertFalse(tracker.equals(null));
        assertFalse(tracker.equals(5));
        assertEquals(tracker.hashCode(), same.hashCode());

        // any difference counts, including the ID counters and the enrollments
        same.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
        assertFalse(tracker.equals(same));

        TuiTracker otherCounters = new TuiTrackerBuilder().withNextIds(9, 9).build();
        assertFalse(new TuiTracker().equals(otherCounters));

        TuiTracker withoutEnrollment = new TuiTracker(tracker);
        withoutEnrollment.deleteLesson(ENGLISH.getId());
        assertFalse(tracker.equals(withoutEnrollment));
    }

    @Test
    public void toStringMethod() {
        String expected = TuiTracker.class.getCanonicalName() + "{students=" + tracker.getStudentList()
                + ", lessons=" + tracker.getLessonList()
                + ", enrollments=" + tracker.getEnrollmentList()
                + ", idGenerator=" + new IdGenerator(5, 6) + "}";
        assertEquals(expected, tracker.toString());
    }

    @Test
    public void builder_keepsTheGivenIds() {
        Lesson lesson = new LessonBuilder().withId(7).build();
        TuiTracker built = new TuiTrackerBuilder().withLesson(lesson).build();

        assertEquals(Optional.of(lesson), built.findLesson(new LessonId(7)));
        assertEquals(8, built.getNextLessonId());
        assertTrue(built.getEnrollmentList().isEmpty());
        assertEquals(Collections.emptyList(), built.getEnrollmentList());
        assertEquals(new Enrollment(ALICE.getId(), MATHEMATICS.getId()), ALICE_MATHEMATICS);
    }
}
