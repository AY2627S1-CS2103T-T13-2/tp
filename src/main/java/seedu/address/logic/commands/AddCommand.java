package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_NOT_IMPLEMENTED;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.Student;
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

    public static final String MESSAGE_SUCCESS =
            "Enrolled %1$s [Student ID: %2$s] in %3$s [Lesson ID: %4$s]. Payment status: %5$s.";
    public static final String MESSAGE_ALREADY_ENROLLED = "The student is already enrolled in this lesson.";
    public static final String MESSAGE_LEVEL_MISMATCH = "Student level does not match lesson level.";

    private static final String PAID = "Paid";
    private static final String UNPAID = "Unpaid";

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

    /**
     * Enrolls the student in the lesson. Every check runs before anything is changed, so a command that fails
     * creates nothing. The first problem found is the one reported, in this order:
     * <ol>
     *   <li>the student exists, then the lesson exists</li>
     *   <li>the student is not already enrolled in the lesson</li>
     *   <li>the student's level is the same as the lesson's level</li>
     * </ol>
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // Creating a new student or a new lesson comes with the next step of the implementation.
        if (!(student instanceof ExistingStudent existingStudent)
                || !(lesson instanceof ExistingLesson existingLesson)) {
            throw new CommandException(MESSAGE_NOT_IMPLEMENTED);
        }

        Student studentToEnroll = model.findStudent(existingStudent.id())
                .orElseThrow(() -> new CommandException(
                        String.format(MESSAGE_STUDENT_NOT_FOUND, existingStudent.id())));
        Lesson lessonToJoin = model.findLesson(existingLesson.id())
                .orElseThrow(() -> new CommandException(
                        String.format(MESSAGE_LESSON_NOT_FOUND, existingLesson.id())));

        if (model.isEnrolled(studentToEnroll.getId(), lessonToJoin.getId())) {
            throw new CommandException(MESSAGE_ALREADY_ENROLLED);
        }
        if (!studentToEnroll.getLevel().equals(lessonToJoin.getLevel())) {
            throw new CommandException(MESSAGE_LEVEL_MISMATCH);
        }

        model.enroll(studentToEnroll.getId(), lessonToJoin.getId());
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToEnroll.getName(), studentToEnroll.getId(),
                lessonToJoin.getSubject(), lessonToJoin.getId(),
                model.isPaid(studentToEnroll.getId()) ? PAID : UNPAID));
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
