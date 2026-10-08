package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.common.Level;
import seedu.address.model.enrollment.Enrollment;
import seedu.address.model.enrollment.EnrollmentList;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.lesson.UniqueLessonList;
import seedu.address.model.lesson.exceptions.LessonNotFoundException;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.UniqueStudentList;
import seedu.address.model.student.exceptions.StudentNotFoundException;

/**
 * Wraps all data at the TuiTracker level: the students, the lessons, the enrollments linking them, and the
 * counters that hand out new IDs.
 *
 * <p>The mutating methods assume the command has already validated its input with the query methods, for example
 * that the student exists and that the levels match. They keep the data consistent: deleting a student or lesson
 * also deletes its enrollments, and IDs are generated here, never chosen by the caller.
 */
public class TuiTracker implements ReadOnlyTuiTracker {

    public static final String MESSAGE_DANGLING_ENROLLMENT =
            "An enrollment refers to a student or lesson that does not exist.";

    private final UniqueStudentList students = new UniqueStudentList();
    private final UniqueLessonList lessons = new UniqueLessonList();
    private final EnrollmentList enrollments = new EnrollmentList();
    private final IdGenerator idGenerator = new IdGenerator();

    public TuiTracker() {}

    /**
     * Creates a TuiTracker using the data in {@code toBeCopied}.
     */
    public TuiTracker(ReadOnlyTuiTracker toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// overwrite operations

    /**
     * Replaces all data with {@code newData}. The ID counters are never set lower than the highest existing ID.
     *
     * @throws IllegalArgumentException if {@code newData} has two students or two lessons with the same ID, the
     *     same enrollment twice, or an enrollment that refers to a student or lesson that does not exist.
     */
    public void resetData(ReadOnlyTuiTracker newData) {
        requireNonNull(newData);

        students.setStudents(newData.getStudentList());
        lessons.setLessons(newData.getLessonList());
        enrollments.setEnrollments(newData.getEnrollmentList());

        for (Enrollment enrollment : enrollments.asUnmodifiableList()) {
            if (!students.contains(enrollment.getStudentId()) || !lessons.contains(enrollment.getLessonId())) {
                throw new IllegalArgumentException(MESSAGE_DANGLING_ENROLLMENT);
            }
        }

        int highestStudentId = newData.getStudentList().stream().mapToInt(s -> s.getId().value).max().orElse(0);
        int highestLessonId = newData.getLessonList().stream().mapToInt(l -> l.getId().value).max().orElse(0);
        idGenerator.reset(Math.max(newData.getNextStudentId(), highestStudentId + 1),
                Math.max(newData.getNextLessonId(), highestLessonId + 1));
    }

    //// queries

    /**
     * Returns the student with the given ID, if there is one.
     */
    public Optional<Student> findStudent(StudentId id) {
        return students.find(id);
    }

    /**
     * Returns the lesson with the given ID, if there is one.
     */
    public Optional<Lesson> findLesson(LessonId id) {
        return lessons.find(id);
    }

    /**
     * Returns the student with the lowest ID who has the same name and contact, if there is one.
     */
    public Optional<Student> findDuplicateStudent(Name name, Contact contact) {
        requireAllNonNull(name, contact);
        return students.asUnmodifiableObservableList().stream()
                .filter(student -> student.getName().equals(name) && student.getContact().equals(contact))
                .findFirst();
    }

    /**
     * Returns the lesson with the lowest ID that has the same subject, level and timeslot, if there is one.
     */
    public Optional<Lesson> findIdenticalLesson(Subject subject, Level level, Timeslot timeslot) {
        requireAllNonNull(subject, level, timeslot);
        return lessons.asUnmodifiableObservableList().stream()
                .filter(lesson -> lesson.getSubject().equals(subject)
                        && lesson.getLevel().equals(level)
                        && lesson.getTimeslot().equals(timeslot))
                .findFirst();
    }

    /**
     * Returns true if the student is enrolled in the lesson.
     */
    public boolean isEnrolled(StudentId studentId, LessonId lessonId) {
        return enrollments.contains(studentId, lessonId);
    }

    /**
     * Returns true if {@code timeslot} overlaps the timeslot of any existing lesson.
     * A lesson that starts exactly when another ends does not clash with it.
     */
    public boolean hasClash(Timeslot timeslot) {
        requireNonNull(timeslot);
        return lessons.asUnmodifiableObservableList().stream()
                .anyMatch(lesson -> lesson.getTimeslot().overlaps(timeslot));
    }

    /**
     * Returns the IDs of the lessons the student is enrolled in, in ascending order.
     */
    public List<LessonId> getLessonIdsOf(StudentId studentId) {
        return enrollments.lessonIdsOf(studentId);
    }

    /**
     * Returns the students enrolled in the lesson, in ascending order of ID.
     */
    public List<Student> getStudentsIn(LessonId lessonId) {
        return enrollments.studentIdsIn(lessonId).stream()
                .map(id -> students.find(id).orElseThrow(StudentNotFoundException::new))
                .collect(Collectors.toList());
    }

    //// mutations

    /**
     * Creates a student with a new ID, who has not been marked as paid, and returns the student.
     */
    public Student addStudent(Name name, Contact contact, Level level) {
        Student student = new Student(idGenerator.newStudentId(), name, contact, level);
        students.add(student);
        return student;
    }

    /**
     * Creates a lesson with a new ID and returns the lesson.
     */
    public Lesson addLesson(Subject subject, Level level, Timeslot timeslot, Fee fee) {
        Lesson lesson = new Lesson(idGenerator.newLessonId(), subject, level, timeslot, fee);
        lessons.add(lesson);
        return lesson;
    }

    /**
     * Enrolls the student in the lesson.
     *
     * @throws StudentNotFoundException if there is no such student.
     * @throws LessonNotFoundException if there is no such lesson.
     * @throws IllegalArgumentException if the student is already enrolled in the lesson.
     */
    public void enroll(StudentId studentId, LessonId lessonId) {
        requireAllNonNull(studentId, lessonId);
        if (!students.contains(studentId)) {
            throw new StudentNotFoundException();
        }
        if (!lessons.contains(lessonId)) {
            throw new LessonNotFoundException();
        }
        enrollments.add(new Enrollment(studentId, lessonId));
    }

    /**
     * Deletes the student and all of the student's enrollments, and returns the deleted student.
     *
     * @throws StudentNotFoundException if there is no such student.
     */
    public Student deleteStudent(StudentId studentId) {
        Student deleted = students.remove(studentId);
        enrollments.removeAllOfStudent(studentId);
        return deleted;
    }

    /**
     * Deletes the lesson and all enrollments in it, and returns the deleted lesson.
     *
     * @throws LessonNotFoundException if there is no such lesson.
     */
    public Lesson deleteLesson(LessonId lessonId) {
        Lesson deleted = lessons.remove(lessonId);
        enrollments.removeAllOfLesson(lessonId);
        return deleted;
    }

    /**
     * Marks the student as paid for {@code month} and returns the updated student.
     *
     * @throws StudentNotFoundException if there is no such student.
     */
    public Student markPaid(StudentId studentId, YearMonth month) {
        requireAllNonNull(studentId, month);
        Student original = students.find(studentId).orElseThrow(StudentNotFoundException::new);
        Student updated = original.markPaid(month);
        students.replace(original, updated);
        return updated;
    }

    //// util methods

    @Override
    public ObservableList<Student> getStudentList() {
        return students.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Lesson> getLessonList() {
        return lessons.asUnmodifiableObservableList();
    }

    @Override
    public List<Enrollment> getEnrollmentList() {
        return enrollments.asUnmodifiableList();
    }

    @Override
    public int getNextStudentId() {
        return idGenerator.getNextStudentId();
    }

    @Override
    public int getNextLessonId() {
        return idGenerator.getNextLessonId();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TuiTracker otherTracker)) {
            return false;
        }

        return students.equals(otherTracker.students)
                && lessons.equals(otherTracker.lessons)
                && enrollments.equals(otherTracker.enrollments)
                && idGenerator.equals(otherTracker.idGenerator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(students, lessons, enrollments, idGenerator);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("students", students)
                .add("lessons", lessons)
                .add("enrollments", enrollments)
                .add("idGenerator", idGenerator)
                .toString();
    }

}
