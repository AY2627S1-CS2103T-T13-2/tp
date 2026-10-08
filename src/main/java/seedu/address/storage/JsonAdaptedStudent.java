package seedu.address.storage;

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
        if (id == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "id"));
        }
        if (!StudentId.isValidId(id)) {
            throw new IllegalValueException(StudentId.MESSAGE_CONSTRAINTS);
        }
        final StudentId modelId = new StudentId(id);

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (contact == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "contact"));
        }
        if (!Contact.isValidContact(contact)) {
            throw new IllegalValueException(Contact.MESSAGE_CONSTRAINTS);
        }
        final Contact modelContact = new Contact(contact);

        if (level == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "level"));
        }
        if (!Level.isValidLevel(level)) {
            throw new IllegalValueException(Level.MESSAGE_CONSTRAINTS_STUDENT);
        }
        final Level modelLevel = new Level(level);

        YearMonth modelLastPaidMonth = null;
        if (lastPaidMonth != null) {
            try {
                modelLastPaidMonth = YearMonth.parse(lastPaidMonth);
            } catch (DateTimeParseException e) {
                throw new IllegalValueException(INVALID_MONTH_MESSAGE);
            }
        }

        return new Student(modelId, modelName, modelContact, modelLevel, modelLastPaidMonth);
    }

}
