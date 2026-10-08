package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.ReadOnlyTuiTracker;
import seedu.address.model.TuiTracker;
import seedu.address.model.enrollment.Enrollment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/**
 * An Immutable TuiTracker that is serializable to JSON format.
 *
 * <p>The file holds the two ID counters, the students, the lessons and the enrollments. Two students (or two
 * lessons) that are duplicates of each other are accepted and listed separately, since a hand-edited file may
 * contain them. Two records with the same ID, or an enrollment that points to a record that does not exist, are
 * rejected.
 */
@JsonRootName(value = "tuitracker")
class JsonSerializableTuiTracker {

    public static final String MESSAGE_INCONSISTENT_DATA = "The data file is inconsistent: %s";

    private final Integer nextStudentId;
    private final Integer nextLessonId;
    private final List<JsonAdaptedStudent> students = new ArrayList<>();
    private final List<JsonAdaptedLesson> lessons = new ArrayList<>();
    private final List<JsonAdaptedEnrollment> enrollments = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableTuiTracker} with the given data. A missing counter is worked out from
     * the highest ID in the file, and a missing list is empty.
     */
    @JsonCreator
    public JsonSerializableTuiTracker(@JsonProperty("nextStudentId") Integer nextStudentId,
            @JsonProperty("nextLessonId") Integer nextLessonId,
            @JsonProperty("students") List<JsonAdaptedStudent> students,
            @JsonProperty("lessons") List<JsonAdaptedLesson> lessons,
            @JsonProperty("enrollments") List<JsonAdaptedEnrollment> enrollments) {
        this.nextStudentId = nextStudentId;
        this.nextLessonId = nextLessonId;
        if (students != null) {
            this.students.addAll(students);
        }
        if (lessons != null) {
            this.lessons.addAll(lessons);
        }
        if (enrollments != null) {
            this.enrollments.addAll(enrollments);
        }
    }

    /**
     * Converts a given {@code ReadOnlyTuiTracker} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableTuiTracker}.
     */
    public JsonSerializableTuiTracker(ReadOnlyTuiTracker source) {
        nextStudentId = source.getNextStudentId();
        nextLessonId = source.getNextLessonId();
        students.addAll(source.getStudentList().stream().map(JsonAdaptedStudent::new).collect(Collectors.toList()));
        lessons.addAll(source.getLessonList().stream().map(JsonAdaptedLesson::new).collect(Collectors.toList()));
        enrollments.addAll(source.getEnrollmentList().stream()
                .map(JsonAdaptedEnrollment::new).collect(Collectors.toList()));
    }

    /**
     * Converts this into the model's {@code TuiTracker} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public TuiTracker toModelType() throws IllegalValueException {
        List<Student> modelStudents = new ArrayList<>();
        for (JsonAdaptedStudent student : students) {
            modelStudents.add(student.toModelType());
        }
        List<Lesson> modelLessons = new ArrayList<>();
        for (JsonAdaptedLesson lesson : lessons) {
            modelLessons.add(lesson.toModelType());
        }
        List<Enrollment> modelEnrollments = new ArrayList<>();
        for (JsonAdaptedEnrollment enrollment : enrollments) {
            modelEnrollments.add(enrollment.toModelType());
        }

        LoadedData loaded = new LoadedData(nextStudentId == null ? 1 : nextStudentId,
                nextLessonId == null ? 1 : nextLessonId, modelStudents, modelLessons, modelEnrollments);
        try {
            return new TuiTracker(loaded);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(String.format(MESSAGE_INCONSISTENT_DATA, e.getMessage()));
        }
    }

    /**
     * The data read from the file, before {@code TuiTracker} has checked that it is consistent.
     */
    private static class LoadedData implements ReadOnlyTuiTracker {
        private final int nextStudentId;
        private final int nextLessonId;
        private final ObservableList<Student> students;
        private final ObservableList<Lesson> lessons;
        private final List<Enrollment> enrollments;

        LoadedData(int nextStudentId, int nextLessonId, List<Student> students, List<Lesson> lessons,
                List<Enrollment> enrollments) {
            this.nextStudentId = Math.max(1, nextStudentId);
            this.nextLessonId = Math.max(1, nextLessonId);
            this.students = FXCollections.observableArrayList(students);
            this.lessons = FXCollections.observableArrayList(lessons);
            this.enrollments = enrollments;
        }

        @Override
        public ObservableList<Student> getStudentList() {
            return students;
        }

        @Override
        public ObservableList<Lesson> getLessonList() {
            return lessons;
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
    }

}
