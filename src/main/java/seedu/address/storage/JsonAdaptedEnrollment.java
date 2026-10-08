package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.enrollment.Enrollment;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

/**
 * Jackson-friendly version of {@link Enrollment}.
 */
class JsonAdaptedEnrollment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Enrollment's %s field is missing!";

    private final Integer studentId;
    private final Integer lessonId;

    /**
     * Constructs a {@code JsonAdaptedEnrollment} with the given IDs.
     */
    @JsonCreator
    public JsonAdaptedEnrollment(@JsonProperty("studentId") Integer studentId,
            @JsonProperty("lessonId") Integer lessonId) {
        this.studentId = studentId;
        this.lessonId = lessonId;
    }

    /**
     * Converts a given {@code Enrollment} into this class for Jackson use.
     */
    public JsonAdaptedEnrollment(Enrollment source) {
        studentId = source.getStudentId().value;
        lessonId = source.getLessonId().value;
    }

    /**
     * Converts this Jackson-friendly adapted enrollment object into the model's {@code Enrollment} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted enrollment.
     */
    public Enrollment toModelType() throws IllegalValueException {
        if (studentId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "studentId"));
        }
        if (!StudentId.isValidId(studentId)) {
            throw new IllegalValueException(StudentId.MESSAGE_CONSTRAINTS);
        }
        if (lessonId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "lessonId"));
        }
        if (!LessonId.isValidId(lessonId)) {
            throw new IllegalValueException(LessonId.MESSAGE_CONSTRAINTS);
        }
        return new Enrollment(new StudentId(studentId), new LessonId(lessonId));
    }

}
