package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_NOT_IMPLEMENTED;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.StudentId;

/**
 * Enrolls a student in a lesson. The student and the lesson can each be new or existing.
 *
 * <p>The command records what the user asked for: an existing record by ID, or the details of a new one. Nothing is
 * created until {@link #execute(Model)} has checked that the whole request is valid. See specification codes ADD-1
 * to ADD-15.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " s/STUDENT l/LESSON [c/CONTACT slv/STUDENT_LEVEL] "
            + "[llv/LESSON_LEVEL d/DAY st/START_TIME et/END_TIME f/FEE]";

    private final StudentRef student;
    private final LessonRef lesson;

    /**
     * Creates a command that enrolls {@code student} in {@code lesson}.
     */
    public AddCommand(StudentRef student, LessonRef lesson) {
        requireAllNonNull(student, lesson);
        this.student = student;
        this.lesson = lesson;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherCommand)) {
            return false;
        }

        return student.equals(otherCommand.student) && lesson.equals(otherCommand.lesson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, lesson);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("student", student)
                .add("lesson", lesson)
                .toString();
    }

    /**
     * The student an {@code add} command refers to: an existing student by ID, or a new student with their details.
     */
    public sealed interface StudentRef permits ExistingStudent, NewStudent { }

    /**
     * An existing student, referred to by ID.
     */
    public record ExistingStudent(StudentId id) implements StudentRef {
        /** Creates a reference to the student with the given ID. */
        public ExistingStudent {
            requireNonNull(id);
        }
    }

    /**
     * A student who does not exist yet, with the details to create them with.
     */
    public record NewStudent(Name name, Contact contact, Level level) implements StudentRef {
        /** Creates the details of a new student. */
        public NewStudent {
            requireAllNonNull(name, contact, level);
        }
    }

    /**
     * The lesson an {@code add} command refers to: an existing lesson by ID, or a new lesson with its details.
     */
    public sealed interface LessonRef permits ExistingLesson, NewLesson { }

    /**
     * An existing lesson, referred to by ID.
     */
    public record ExistingLesson(LessonId id) implements LessonRef {
        /** Creates a reference to the lesson with the given ID. */
        public ExistingLesson {
            requireNonNull(id);
        }
    }

    /**
     * A lesson that does not exist yet, with the details to create it with.
     */
    public record NewLesson(Subject subject, Level level, Timeslot timeslot, Fee fee) implements LessonRef {
        /** Creates the details of a new lesson. */
        public NewLesson {
            requireAllNonNull(subject, level, timeslot, fee);
        }
    }
}
