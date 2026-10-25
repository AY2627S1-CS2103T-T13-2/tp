package seedu.address.storage;

import static seedu.address.storage.JsonFields.validated;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.common.Level;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Jackson-friendly version of {@link Student}.
 */
class JsonAdaptedStudent {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Student's %s field is missing!";
    public static final String INVALID_MONTH_MESSAGE = "Student's lastPaidMonth must be written as yyyy-MM.";

    private final Integer id;
    private final String name;
    private final String contact;
    private final String level;
    private final String lastPaidMonth;

    /**
     * Constructs a {@code JsonAdaptedStudent} with the given student details.
     */
    @JsonCreator
    public JsonAdaptedStudent(@JsonProperty("id") Integer id, @JsonProperty("name") String name,
            @JsonProperty("contact") String contact, @JsonProperty("level") String level,
            @JsonProperty("lastPaidMonth") String lastPaidMonth) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.level = level;
        this.lastPaidMonth = lastPaidMonth;
    }

    /**
     * Converts a given {@code Student} into this class for Jackson use.
     */
    public JsonAdaptedStudent(Student source) {
        id = source.getId().value;
        name = source.getName().fullName;
        contact = source.getContact().value;
        level = source.getLevel().value;
        lastPaidMonth = source.getLastPaidMonth().map(YearMonth::toString).orElse(null);
    }

    /**
     * Converts this Jackson-friendly adapted student object into the model's {@code Student} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted student.
     */
    public Student toModelType() throws IllegalValueException {
        StudentId modelId = validated(required("id", id),
                StudentId::isValidId, StudentId.MESSAGE_CONSTRAINTS, StudentId::new);
        Name modelName = validated(required("name", name),
                Name::isValidName, Name.MESSAGE_CONSTRAINTS, Name::new);
        Contact modelContact = validated(required("contact", contact),
                Contact::isValidContact, Contact.MESSAGE_CONSTRAINTS, Contact::new);
        Level modelLevel = validated(required("level", level),
                Level::isValidLevel, Level.MESSAGE_CONSTRAINTS_STUDENT, Level::new);

        return new Student(modelId, modelName, modelContact, modelLevel, toLastPaidMonth());
    }

    /**
     * Returns the month the student last paid, or null if they have not paid yet.
     */
    private YearMonth toLastPaidMonth() throws IllegalValueException {
        if (lastPaidMonth == null) {
            return null;
        }
        try {
            return YearMonth.parse(lastPaidMonth);
        } catch (DateTimeParseException e) {
            throw new IllegalValueException(INVALID_MONTH_MESSAGE);
        }
    }

    private static <T> T required(String fieldName, T value) throws IllegalValueException {
        return JsonFields.required(value, MISSING_FIELD_MESSAGE_FORMAT, fieldName);
    }

}
