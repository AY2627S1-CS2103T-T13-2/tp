package seedu.address.model.lesson;

import seedu.address.model.common.RecordId;

/**
 * Represents the permanent identifier of a Lesson: a positive whole number.
 * Lesson IDs are numbered separately from student IDs.
 * Guarantees: immutable; is valid as declared in {@link RecordId#isValidId(int)}
 */
public class LessonId extends RecordId<LessonId> {

    public static final String MESSAGE_CONSTRAINTS = "Lesson ID must be a positive whole number.";

    /**
     * Constructs a {@code LessonId}.
     *
     * @param value A valid ID.
     */
    public LessonId(int value) {
        super(value, MESSAGE_CONSTRAINTS);
    }

}
