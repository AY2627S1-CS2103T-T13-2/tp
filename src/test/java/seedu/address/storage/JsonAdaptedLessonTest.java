package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedLesson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;

public class JsonAdaptedLessonTest {

    private static final Integer VALID_ID = 1;
    private static final String VALID_SUBJECT = "Mathematics";
    private static final String VALID_LEVEL = "Primary 6";
    private static final String VALID_DAY = "Monday";
    private static final String VALID_START = "15:00";
    private static final String VALID_END = "16:30";
    private static final String VALID_FEE = "50.00";

    private static JsonAdaptedLesson lesson(Integer id, String subject, String level, String day, String start,
            String end, String fee) {
        return new JsonAdaptedLesson(id, subject, level, day, start, end, fee);
    }

    @Test
    public void toModelType_validLessonDetails_returnsLesson() throws Exception {
        assertEquals(MATHEMATICS, new JsonAdaptedLesson(MATHEMATICS).toModelType());
        assertEquals(MATHEMATICS, lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, VALID_DAY, VALID_START, VALID_END,
                VALID_FEE).toModelType());
    }

    @Test
    public void toModelType_dayIsCaseInsensitive() throws Exception {
        assertEquals(MATHEMATICS, lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, "MONDAY", VALID_START, VALID_END,
                VALID_FEE).toModelType());
    }

    @Test
    public void toModelType_missingFields_throwsIllegalValueException() {
        assertMissing("id", lesson(null, VALID_SUBJECT, VALID_LEVEL, VALID_DAY, VALID_START, VALID_END, VALID_FEE));
        assertMissing("subject", lesson(VALID_ID, null, VALID_LEVEL, VALID_DAY, VALID_START, VALID_END, VALID_FEE));
        assertMissing("level", lesson(VALID_ID, VALID_SUBJECT, null, VALID_DAY, VALID_START, VALID_END, VALID_FEE));
        assertMissing("day", lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, null, VALID_START, VALID_END, VALID_FEE));
        assertMissing("start", lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, VALID_DAY, null, VALID_END, VALID_FEE));
        assertMissing("end", lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, VALID_DAY, VALID_START, null, VALID_FEE));
        assertMissing("fee", lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, VALID_DAY, VALID_START, VALID_END, null));
    }

    @Test
    public void toModelType_invalidValues_throwsIllegalValueException() {
        assertInvalid(LessonId.MESSAGE_CONSTRAINTS,
                lesson(0, VALID_SUBJECT, VALID_LEVEL, VALID_DAY, VALID_START, VALID_END, VALID_FEE));
        assertInvalid(Subject.MESSAGE_CONSTRAINTS,
                lesson(VALID_ID, "101", VALID_LEVEL, VALID_DAY, VALID_START, VALID_END, VALID_FEE));
        assertInvalid(Level.MESSAGE_CONSTRAINTS_LESSON,
                lesson(VALID_ID, VALID_SUBJECT, "---", VALID_DAY, VALID_START, VALID_END, VALID_FEE));
        assertInvalid(Timeslot.MESSAGE_INVALID_DAY,
                lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, "Mon", VALID_START, VALID_END, VALID_FEE));
        assertInvalid(Timeslot.MESSAGE_INVALID_START_TIME,
                lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, VALID_DAY, "3pm", VALID_END, VALID_FEE));
        assertInvalid(Timeslot.MESSAGE_INVALID_END_TIME,
                lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, VALID_DAY, VALID_START, "9:00", VALID_FEE));
        assertInvalid(Fee.MESSAGE_CONSTRAINTS,
                lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, VALID_DAY, VALID_START, VALID_END, "0"));
    }

    @Test
    public void toModelType_endNotLaterThanStart_throwsIllegalValueException() {
        assertInvalid(Timeslot.MESSAGE_INVALID_END_TIME,
                lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, VALID_DAY, "16:30", "15:00", VALID_FEE));
        assertInvalid(Timeslot.MESSAGE_INVALID_END_TIME,
                lesson(VALID_ID, VALID_SUBJECT, VALID_LEVEL, VALID_DAY, "15:00", "15:00", VALID_FEE));
    }

    private static void assertMissing(String field, JsonAdaptedLesson lesson) {
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, field),
                lesson::toModelType);
    }

    private static void assertInvalid(String expectedMessage, JsonAdaptedLesson lesson) {
        assertThrows(IllegalValueException.class, expectedMessage, lesson::toModelType);
    }
}
