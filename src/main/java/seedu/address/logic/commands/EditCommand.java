package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

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
 * Edits the details of an existing student or lesson while preserving its ID and related data.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + " student ID [s/NAME] [c/CONTACT] [slv/STUDENT_LEVEL] OR "
            + COMMAND_WORD
            + " lesson ID [l/SUBJECT] [llv/LESSON_LEVEL] [d/DAY] [st/START_TIME] [et/END_TIME] [f/FEE]";

    public static final String MESSAGE_SUCCESS_STUDENT = "Edited student: %s (ID: %s).";
    public static final String MESSAGE_SUCCESS_LESSON = "Edited lesson: %s | %s | %s (ID: %s).";
    public static final String MESSAGE_NO_CHANGES = "No changes were made.";
    public static final String MESSAGE_NO_FIELDS = "At least one field to edit must be provided.";
    public static final String MESSAGE_STUDENT_PARAMETERS_NOT_ALLOWED =
            "Only s/, c/ and slv/ can be used when editing a student.";
    public static final String MESSAGE_LESSON_PARAMETERS_NOT_ALLOWED =
            "Only l/, llv/, d/, st/, et/ and f/ can be used when editing a lesson.";
    public static final String MESSAGE_DUPLICATE_STUDENT =
            "Another student with the same name and contact already exists (Student ID: %s).";
    public static final String MESSAGE_DUPLICATE_LESSON =
            "Another identical lesson already exists (Lesson ID: %s).";
    public static final String MESSAGE_STUDENT_LEVEL_MISMATCH =
            "Student level does not match an enrolled lesson.";
    public static final String MESSAGE_LESSON_LEVEL_MISMATCH =
            "Lesson level does not match an enrolled student.";
    public static final String MESSAGE_LESSON_CLASH = "Edited lesson clashes with another lesson.";

    private final EditTarget target;

    /** Creates a command that applies the changes in {@code target}. */
    public EditCommand(EditTarget target) {
        this.target = requireNonNull(target);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        return target instanceof StudentEdit studentEdit
                ? editStudent(model, studentEdit)
                : editLesson(model, (LessonEdit) target);
    }

    private CommandResult editStudent(Model model, StudentEdit edit) throws CommandException {
        Student original = model.findStudent(edit.id()).orElseThrow(() ->
                new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, edit.id())));
        Name name = edit.name().orElse(original.getName());
        Contact contact = edit.contact().orElse(original.getContact());
        Level level = edit.level().orElse(original.getLevel());
        Student updated = new Student(original.getId(), name, contact, level,
                original.getLastPaidMonth().orElse(null));

        if (updated.equals(original)) {
            return new CommandResult(MESSAGE_NO_CHANGES);
        }

        Optional<Student> duplicate = model.getTuiTracker().getStudentList().stream()
                .filter(student -> !student.getId().equals(original.getId()))
                .filter(student -> student.getName().equals(name) && student.getContact().equals(contact))
                .findFirst();
        if (duplicate.isPresent()) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_STUDENT, duplicate.get().getId()));
        }

        boolean mismatchedLesson = model.getLessonIdsOf(original.getId()).stream()
                .map(id -> model.findLesson(id).orElseThrow())
                .anyMatch(lesson -> !lesson.getLevel().equals(level));
        if (mismatchedLesson) {
            throw new CommandException(MESSAGE_STUDENT_LEVEL_MISMATCH);
        }

        Student edited = model.editStudent(original.getId(), name, contact, level);
        return new CommandResult(String.format(MESSAGE_SUCCESS_STUDENT, edited.getName(), edited.getId()));
    }

    private CommandResult editLesson(Model model, LessonEdit edit) throws CommandException {
        Lesson original = model.findLesson(edit.id()).orElseThrow(() ->
                new CommandException(String.format(MESSAGE_LESSON_NOT_FOUND, edit.id())));
        Subject subject = edit.subject().orElse(original.getSubject());
        Level level = edit.level().orElse(original.getLevel());
        DayOfWeek day = edit.day().orElse(original.getTimeslot().getDay());
        LocalTime start = edit.start().orElse(original.getTimeslot().getStart());
        LocalTime end = edit.end().orElse(original.getTimeslot().getEnd());
        if (!Timeslot.isValidTimeslot(start, end)) {
            throw new CommandException(Timeslot.MESSAGE_INVALID_END_TIME);
        }
        Timeslot timeslot = new Timeslot(day, start, end);
        Fee fee = edit.fee().orElse(original.getFee());
        Lesson updated = new Lesson(original.getId(), subject, level, timeslot, fee);

        if (updated.equals(original)) {
            return new CommandResult(MESSAGE_NO_CHANGES);
        }

        Optional<Lesson> duplicate = model.getTuiTracker().getLessonList().stream()
                .filter(lesson -> !lesson.getId().equals(original.getId()))
                .filter(lesson -> lesson.getSubject().equals(subject)
                        && lesson.getLevel().equals(level)
                        && lesson.getTimeslot().equals(timeslot))
                .findFirst();
        if (duplicate.isPresent()) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_LESSON, duplicate.get().getId()));
        }

        boolean mismatchedStudent = model.getStudentsIn(original.getId()).stream()
                .anyMatch(student -> !student.getLevel().equals(level));
        if (mismatchedStudent) {
            throw new CommandException(MESSAGE_LESSON_LEVEL_MISMATCH);
        }

        boolean clashes = model.getTuiTracker().getLessonList().stream()
                .filter(lesson -> !lesson.getId().equals(original.getId()))
                .anyMatch(lesson -> lesson.getTimeslot().overlaps(timeslot));
        if (clashes) {
            throw new CommandException(MESSAGE_LESSON_CLASH);
        }

        Lesson edited = model.editLesson(original.getId(), subject, level, timeslot, fee);
        return new CommandResult(String.format(MESSAGE_SUCCESS_LESSON, edited.getSubject(), edited.getLevel(),
                edited.getTimeslot(), edited.getId()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof EditCommand otherCommand && target.equals(otherCommand.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target);
    }

    /** The student or lesson edit requested by the user. */
    public sealed interface EditTarget permits StudentEdit, LessonEdit { }

    /** Fields to change on one student. Empty fields retain their stored values. */
    public record StudentEdit(StudentId id, Optional<Name> name, Optional<Contact> contact,
                              Optional<Level> level) implements EditTarget {
        /** Creates an edit request for a student. */
        public StudentEdit {
            requireAllNonNull(id, name, contact, level);
        }
    }

    /** Fields to change on one lesson. Empty fields retain their stored values. */
    public record LessonEdit(LessonId id, Optional<Subject> subject, Optional<Level> level,
                             Optional<DayOfWeek> day, Optional<LocalTime> start, Optional<LocalTime> end,
                             Optional<Fee> fee) implements EditTarget {
        /** Creates an edit request for a lesson. */
        public LessonEdit {
            requireAllNonNull(id, subject, level, day, start, end, fee);
        }
    }
}
