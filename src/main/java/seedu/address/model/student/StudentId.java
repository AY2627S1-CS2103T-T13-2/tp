package seedu.address.model.student;

import seedu.address.model.common.RecordId;

/**
 * Represents the permanent identifier of a Student: a positive whole number.
 * Student IDs are numbered separately from lesson IDs.
 * Guarantees: immutable; is valid as declared in {@link RecordId#isValidId(int)}
 */
public class StudentId extends RecordId<StudentId> {

    public static final String MESSAGE_CONSTRAINTS = "Student ID must be a positive whole number.";

    /**
     * Constructs a {@code StudentId}.
     *
     * @param value A valid ID.
     */
    public StudentId(int value) {
        super(value, MESSAGE_CONSTRAINTS);
    }

}
