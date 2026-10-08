package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedEnrollment.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEnrollments.ALICE_MATHEMATICS;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

public class JsonAdaptedEnrollmentTest {

    @Test
    public void toModelType_validDetails_returnsEnrollment() throws Exception {
        assertEquals(ALICE_MATHEMATICS, new JsonAdaptedEnrollment(ALICE_MATHEMATICS).toModelType());
        assertEquals(ALICE_MATHEMATICS, new JsonAdaptedEnrollment(1, 1).toModelType());
    }

    @Test
    public void toModelType_missingIds_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "studentId"), () ->
                new JsonAdaptedEnrollment(null, 1).toModelType());
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "lessonId"), () ->
                new JsonAdaptedEnrollment(1, null).toModelType());
    }

    @Test
    public void toModelType_idNotPositive_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, StudentId.MESSAGE_CONSTRAINTS, () ->
                new JsonAdaptedEnrollment(0, 1).toModelType());
        assertThrows(IllegalValueException.class, LessonId.MESSAGE_CONSTRAINTS, () ->
                new JsonAdaptedEnrollment(1, -3).toModelType());
    }
}
