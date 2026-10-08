package seedu.address.model.student;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.YearMonth;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.common.Level;

/**
 * Represents a Student in TuiTracker.
 * A student is paid for a month if they were last marked as paid in that month, so the payment status resets
 * by itself when a new month starts.
 * Guarantees: details are present and not null (except {@code lastPaidMonth}, which is null if never paid);
 * field values are validated; immutable.
 */
public class Student {

    private final StudentId id;
    private final Name name;
    private final Contact contact;
    private final Level level;
    private final YearMonth lastPaidMonth;

    /**
     * Creates a student who has never been marked as paid. Every field must be present and not null.
     */
    public Student(StudentId id, Name name, Contact contact, Level level) {
        this(id, name, contact, level, null);
    }

    /**
     * Creates a student. Every field except {@code lastPaidMonth} must be present and not null.
     *
     * @param lastPaidMonth The month the student was last marked as paid, or null if never.
     */
    public Student(StudentId id, Name name, Contact contact, Level level, YearMonth lastPaidMonth) {
        requireAllNonNull(id, name, contact, level);
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.level = level;
        this.lastPaidMonth = lastPaidMonth;
    }

    public StudentId getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Contact getContact() {
        return contact;
    }

    public Level getLevel() {
        return level;
    }

    public Optional<YearMonth> getLastPaidMonth() {
        return Optional.ofNullable(lastPaidMonth);
    }

    /**
     * Returns true if this student was marked as paid in {@code month}.
     */
    public boolean isPaid(YearMonth month) {
        return month.equals(lastPaidMonth);
    }

    /**
     * Returns a copy of this student that was marked as paid in {@code month}.
     */
    public Student markPaid(YearMonth month) {
        return new Student(id, name, contact, level, month);
    }

    /**
     * Returns true if both students have the same name and contact. This is the duplicate rule used by
     * {@code add}; it is separate from the ID, which always differs between two students.
     */
    public boolean isSameStudent(Student other) {
        return other == this
                || (other != null && name.equals(other.name) && contact.equals(other.contact));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Student otherStudent)) {
            return false;
        }

        return id.equals(otherStudent.id)
                && name.equals(otherStudent.name)
                && contact.equals(otherStudent.contact)
                && level.equals(otherStudent.level)
                && Objects.equals(lastPaidMonth, otherStudent.lastPaidMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, contact, level, lastPaidMonth);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .add("contact", contact)
                .add("level", level)
                .add("lastPaidMonth", lastPaidMonth)
                .toString();
    }

}
