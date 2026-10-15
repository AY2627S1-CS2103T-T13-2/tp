package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import java.util.Objects;
import java.util.Optional;

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
    public static final String MESSAGE_DUPLICATE_STUDENT = "A student with the same name and contact already "
            + "exists (Student ID: %1$s). Use the existing student ID.";
    public static final String MESSAGE_DUPLICATE_LESSON =
            "An identical lesson already exists (Lesson ID: %1$s). Use the existing lesson ID.";
    public static final String MESSAGE_ALREADY_ENROLLED = "The student is already enrolled in this lesson.";
    public static final String MESSAGE_LEVEL_MISMATCH = "Student level does not match lesson level.";
    public static final String MESSAGE_LESSON_CLASH = "Lesson clashes with an existing lesson.";

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
     * Enrolls the student in the lesson, creating the student and the lesson first if they are new.
     * Every check runs before anything is created or changed, so a command that fails leaves everything as it was
     * and uses up no ID. The first problem found is the one reported, in this order:
     * <ol>
     *   <li>the student exists, then the lesson exists (only for an existing student or lesson)</li>
     *   <li>a new student is not a duplicate, then a new lesson is not a duplicate, then the student is not
     *       already enrolled in the lesson (only possible when both already exist)</li>
     *   <li>the student's level is the same as the lesson's level</li>
     *   <li>a new lesson does not clash with an existing lesson</li>
     * </ol>
     * The duplicate check comes before the clash check because an identical lesson always overlaps the existing
     * one, so the clash check would otherwise hide the more helpful duplicate message.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Optional<Student> existingStudent = findExistingStudent(model);
        Optional<Lesson> existingLesson = findExistingLesson(model);

        checkForDuplicates(model, existingStudent, existingLesson);

        if (!levelOfStudent(existingStudent).equals(levelOfLesson(existingLesson))) {
            throw new CommandException(MESSAGE_LEVEL_MISMATCH);
        }

        if (lesson instanceof NewLesson newLesson && model.hasClash(newLesson.timeslot())) {
            throw new CommandException(MESSAGE_LESSON_CLASH);
        }

        // The whole request is valid, so create what is new and enroll.
        Student studentToEnroll = student instanceof NewStudent newStudent
                ? model.addStudent(newStudent.name(), newStudent.contact(), newStudent.level())
                : existingStudent.orElseThrow();
        Lesson lessonToJoin = lesson instanceof NewLesson newLesson
                ? model.addLesson(newLesson.subject(), newLesson.level(), newLesson.timeslot(), newLesson.fee())
                : existingLesson.orElseThrow();

        model.enroll(studentToEnroll.getId(), lessonToJoin.getId());
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToEnroll.getName(), studentToEnroll.getId(),
                lessonToJoin.getSubject(), lessonToJoin.getId(),
                model.isPaid(studentToEnroll.getId()) ? PAID : UNPAID));
    }

    /**
     * Returns the student this command refers to by ID, or an empty {@code Optional} if the student is new.
     *
     * @throws CommandException if the student is referred to by an ID that does not exist.
     */
    private Optional<Student> findExistingStudent(Model model) throws CommandException {
        if (!(student instanceof ExistingStudent existing)) {
            return Optional.empty();
        }
        return Optional.of(model.findStudent(existing.id()).orElseThrow(() ->
                new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, existing.id()))));
    }

    /**
     * Returns the lesson this command refers to by ID, or an empty {@code Optional} if the lesson is new.
     *
     * @throws CommandException if the lesson is referred to by an ID that does not exist.
     */
    private Optional<Lesson> findExistingLesson(Model model) throws CommandException {
        if (!(lesson instanceof ExistingLesson existing)) {
            return Optional.empty();
        }
        return Optional.of(model.findLesson(existing.id()).orElseThrow(() ->
                new CommandException(String.format(MESSAGE_LESSON_NOT_FOUND, existing.id()))));
    }

    /**
     * Throws if a new student or a new lesson already exists, or if the student is already enrolled in the lesson.
     */
    private void checkForDuplicates(Model model, Optional<Student> existingStudent, Optional<Lesson> existingLesson)
            throws CommandException {
        if (student instanceof NewStudent newStudent) {
            Optional<Student> duplicate = model.findDuplicateStudent(newStudent.name(), newStudent.contact());
            if (duplicate.isPresent()) {
                throw new CommandException(String.format(MESSAGE_DUPLICATE_STUDENT, duplicate.get().getId()));
            }
        }
        if (lesson instanceof NewLesson newLesson) {
            Optional<Lesson> duplicate =
                    model.findIdenticalLesson(newLesson.subject(), newLesson.level(), newLesson.timeslot());
            if (duplicate.isPresent()) {
                throw new CommandException(String.format(MESSAGE_DUPLICATE_LESSON, duplicate.get().getId()));
            }
        }
        if (existingStudent.isPresent() && existingLesson.isPresent()
                && model.isEnrolled(existingStudent.get().getId(), existingLesson.get().getId())) {
            throw new CommandException(MESSAGE_ALREADY_ENROLLED);
        }
    }

    /**
     * Returns the student's level: the stored level of an existing student, or the level given for a new one.
     */
    private Level levelOfStudent(Optional<Student> existingStudent) {
        return student instanceof NewStudent newStudent
                ? newStudent.level()
                : existingStudent.orElseThrow().getLevel();
    }

    /**
     * Returns the lesson's level: the stored level of an existing lesson, or the level given for a new one.
     */
    private Level levelOfLesson(Optional<Lesson> existingLesson) {
        return lesson instanceof NewLesson newLesson
                ? newLesson.level()
                : existingLesson.orElseThrow().getLevel();
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
