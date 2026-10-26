package seedu.address.model.lesson;

import java.util.List;

import seedu.address.model.common.UniqueIdList;
import seedu.address.model.lesson.exceptions.LessonNotFoundException;

/**
 * A list of lessons in which no two lessons have the same ID, kept in ascending order of ID.
 * Two lessons with the same subject, level and timeslot are allowed here: {@code add} rejects them, but a hand-edited
 * data file can contain them, and they are then listed separately instead of being merged.
 */
public class UniqueLessonList extends UniqueIdList<Lesson, LessonId> {

    public static final String MESSAGE_DUPLICATE_ID = "Lessons must have different IDs.";

    private static final String MESSAGE_REPLACEMENT_ID = "A replacement lesson must keep the same ID.";

    /**
     * Creates an empty list of lessons.
     */
    public UniqueLessonList() {
        super(Lesson::getId, LessonNotFoundException::new, MESSAGE_DUPLICATE_ID, MESSAGE_REPLACEMENT_ID);
    }

    /**
     * Replaces the contents of this list with {@code lessons}, sorted by ID.
     *
     * @throws IllegalArgumentException if {@code lessons} contains two lessons with the same ID.
     */
    public void setLessons(List<Lesson> lessons) {
        setAll(lessons);
    }

}
