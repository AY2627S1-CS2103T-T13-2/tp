package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedStudent.INVALID_MONTH_MESSAGE;
import static seedu.address.storage.JsonAdaptedStudent.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.common.Level;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.StudentId;

public class JsonAdaptedStudentTest {

    private static final Integer VALID_ID = ALICE.getId().value;
    private static final String VALID_NAME = ALICE.getName().toString();
    private static final String VALID_CONTACT = ALICE.getContact().toString();
    private static final String VALID_LEVEL = ALICE.getLevel().toString();

    @Test
    public void toModelType_validStudentDetails_returnsStudent() throws Exception {
        assertEquals(ALICE, new JsonAdaptedStudent(ALICE).toModelType());
    }

    @Test
    public void toModelType_studentWhoHasPaid_keepsTheMonth() throws Exception {
        assertEquals(BRYAN, new JsonAdaptedStudent(BRYAN).toModelType());
    }

    @Test
    public void toModelType_noLastPaidMonth_neverPaid() throws Exception {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_ID, VALID_NAME, VALID_CONTACT, VALID_LEVEL, null);

        assertEquals(ALICE, student.toModelType());
    }

    @Test
    public void toModelType_missingId_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(null, VALID_NAME, VALID_CONTACT, VALID_LEVEL, null);
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "id"),
                student::toModelType);
    }

    @Test
    public void toModelType_idNotPositive_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(0, VALID_NAME, VALID_CONTACT, VALID_LEVEL, null);
        assertThrows(IllegalValueException.class, StudentId.MESSAGE_CONSTRAINTS, student::toModelType);
    }

    @Test
    public void toModelType_missingName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_ID, null, VALID_CONTACT, VALID_LEVEL, null);
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"),
                student::toModelType);
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_ID, "12345", VALID_CONTACT, VALID_LEVEL, null);
        assertThrows(IllegalValueException.class, Name.MESSAGE_CONSTRAINTS, student::toModelType);
    }

    @Test
    public void toModelType_missingContact_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_ID, VALID_NAME, null, VALID_LEVEL, null);
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "contact"),
                student::toModelType);
    }

    @Test
    public void toModelType_invalidContact_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_ID, VALID_NAME, "9", VALID_LEVEL, null);
        assertThrows(IllegalValueException.class, Contact.MESSAGE_CONSTRAINTS, student::toModelType);
    }

    @Test
    public void toModelType_missingLevel_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_ID, VALID_NAME, VALID_CONTACT, null, null);
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "level"),
                student::toModelType);
    }

    @Test
    public void toModelType_invalidLevel_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_ID, VALID_NAME, VALID_CONTACT, "---", null);
        assertThrows(IllegalValueException.class, Level.MESSAGE_CONSTRAINTS_STUDENT, student::toModelType);
    }

    @Test
    public void toModelType_invalidLastPaidMonth_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_ID, VALID_NAME, VALID_CONTACT, VALID_LEVEL,
                "October 2026");
        assertThrows(IllegalValueException.class, INVALID_MONTH_MESSAGE, student::toModelType);
    }
}
