package seedu.address.model.lesson;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.common.Level;

/**
 * Represents a Lesson in TuiTracker: a weekly class with a subject, a level, a timeslot and a fee per session.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Lesson {

    private final LessonId id;
    private final Subject subject;
    private final Level level;
    private final Timeslot timeslot;
    private final Fee fee;

    /**
     * Creates a lesson. Every field must be present and not null.
     */
    public Lesson(LessonId id, Subject subject, Level level, Timeslot timeslot, Fee fee) {
        requireAllNonNull(id, subject, level, timeslot, fee);
        this.id = id;
        this.subject = subject;
        this.level = level;
        this.timeslot = timeslot;
        this.fee = fee;
    }

    public LessonId getId() {
        return id;
    }

    public Subject getSubject() {
        return subject;
    }

    public Level getLevel() {
        return level;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public Fee getFee() {
        return fee;
    }

    /**
     * Returns true if both lessons have the same subject, level and timeslot. This is the duplicate rule used by
     * {@code add}; it is separate from the ID, which always differs between two lessons.
     */
    public boolean isSameLesson(Lesson other) {
        return other == this
                || (other != null
                && subject.equals(other.subject)
                && level.equals(other.level)
                && timeslot.equals(other.timeslot));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Lesson otherLesson)) {
            return false;
        }

        return id.equals(otherLesson.id)
                && subject.equals(otherLesson.subject)
                && level.equals(otherLesson.level)
                && timeslot.equals(otherLesson.timeslot)
                && fee.equals(otherLesson.fee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subject, level, timeslot, fee);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("subject", subject)
                .add("level", level)
                .add("timeslot", timeslot)
                .add("fee", fee)
                .toString();
    }

}
