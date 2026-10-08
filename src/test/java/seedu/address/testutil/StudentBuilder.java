package seedu.address.testutil;

import java.time.YearMonth;

import seedu.address.model.common.Level;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * A utility class to help with building Student objects.
 */
public class StudentBuilder {

    public static final int DEFAULT_ID = 1;
    public static final String DEFAULT_NAME = "Alice Tan";
    public static final String DEFAULT_CONTACT = "91234567";
    public static final String DEFAULT_LEVEL = "Primary 6";

    private StudentId id;
    private Name name;
    private Contact contact;
    private Level level;
    private YearMonth lastPaidMonth;

    /**
     * Creates a {@code StudentBuilder} with the default details.
     */
    public StudentBuilder() {
        id = new StudentId(DEFAULT_ID);
        name = new Name(DEFAULT_NAME);
        contact = new Contact(DEFAULT_CONTACT);
        level = new Level(DEFAULT_LEVEL);
        lastPaidMonth = null;
    }

    /**
     * Initializes the StudentBuilder with the data of {@code studentToCopy}.
     */
    public StudentBuilder(Student studentToCopy) {
        id = studentToCopy.getId();
        name = studentToCopy.getName();
        contact = studentToCopy.getContact();
        level = studentToCopy.getLevel();
        lastPaidMonth = studentToCopy.getLastPaidMonth().orElse(null);
    }

    /**
     * Sets the {@code StudentId} of the {@code Student} that we are building.
     */
    public StudentBuilder withId(int id) {
        this.id = new StudentId(id);
        return this;
    }

    /**
     * Sets the {@code Name} of the {@code Student} that we are building.
     */
    public StudentBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code Contact} of the {@code Student} that we are building.
     */
    public StudentBuilder withContact(String contact) {
        this.contact = new Contact(contact);
        return this;
    }

    /**
     * Sets the {@code Level} of the {@code Student} that we are building.
     */
    public StudentBuilder withLevel(String level) {
        this.level = new Level(level);
        return this;
    }

    /**
     * Sets the month the {@code Student} that we are building was last marked as paid.
     */
    public StudentBuilder withLastPaidMonth(YearMonth lastPaidMonth) {
        this.lastPaidMonth = lastPaidMonth;
        return this;
    }

    public Student build() {
        return new Student(id, name, contact, level, lastPaidMonth);
    }

}
