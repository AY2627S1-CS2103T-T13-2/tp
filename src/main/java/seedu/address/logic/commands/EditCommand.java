package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
    public static final String MESSAGE_LESSON_CLASH = AddCommand.MESSAGE_LESSON_CLASH;
    public static final String MESSAGE_NOTE_SAME_NAME = AddCommand.MESSAGE_NOTE_SAME_NAME;
    public static final String MESSAGE_NOTE_LEVEL_MISMATCH = AddCommand.MESSAGE_NOTE_LEVEL_MISMATCH;
    public static final String MESSAGE_NOTE_STUDENT_LEVEL_MISMATCH =
            "Note: %1$s's level (%2$s) differs from lesson %3$s's level (%4$s).";
    public static final String MESSAGE_NOTE_FREE_LESSON = AddCommand.MESSAGE_NOTE_FREE_LESSON;

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
        assert original.getId().equals(edit.id()) : "Student lookup returned a different ID";
        Name name = edit.name().orElse(original.getName());
        Contact contact = edit.contact().orElse(original.getContact());
        Level level = edit.level().orElse(original.getLevel());
        Student updated = new Student(original.getId(), name, contact, level,
                original.getLastPaidMonth().orElse(null));

        if (updated.equals(original) && !hasDifferentLevelSpelling(original.getLevel(), edit.level())) {
            return new CommandResult(MESSAGE_NO_CHANGES);
        }

        Optional<Student> duplicate = findDuplicateStudent(model, original.getId(), name, contact);
        if (duplicate.isPresent()) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_STUDENT, duplicate.get().getId()));
        }

        List<String> notes = new ArrayList<>();
        noteForNamesake(model, edit, original.getId(), name, contact).ifPresent(notes::add);
        notes.addAll(notesForStudentLevelMismatches(model, edit, updated));

        Student edited = model.editStudent(original.getId(), name, contact, level);
        assert updated.equals(edited) : "Model returned unexpected student details after edit";
        String success = String.format(MESSAGE_SUCCESS_STUDENT, edited.getName(), edited.getId());
        return new CommandResult(withNotes(success, notes));
    }

    private CommandResult editLesson(Model model, LessonEdit edit) throws CommandException {
        Lesson original = model.findLesson(edit.id()).orElseThrow(() ->
                new CommandException(String.format(MESSAGE_LESSON_NOT_FOUND, edit.id())));
        assert original.getId().equals(edit.id()) : "Lesson lookup returned a different ID";
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

        if (updated.equals(original) && !hasDifferentLevelSpelling(original.getLevel(), edit.level())) {
            return new CommandResult(MESSAGE_NO_CHANGES);
        }

        Optional<Lesson> duplicate = findDuplicateLesson(model, original.getId(), subject, level, timeslot);
        if (duplicate.isPresent()) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_LESSON, duplicate.get().getId()));
        }

        Optional<Lesson> clash = findClashingLesson(model, original.getId(), timeslot);
        if (clash.isPresent()) {
            Lesson other = clash.get();
            throw new CommandException(String.format(MESSAGE_LESSON_CLASH, other.getId(), other.getSubject(),
                    other.getTimeslot()));
        }

        List<String> notes = notesForLessonLevelMismatches(model, edit, updated);
        noteForFreeLesson(edit, fee).ifPresent(notes::add);

        Lesson edited = model.editLesson(original.getId(), subject, level, timeslot, fee);
        assert updated.equals(edited) : "Model returned unexpected lesson details after edit";
        String success = String.format(MESSAGE_SUCCESS_LESSON, edited.getSubject(), edited.getLevel(),
                edited.getTimeslot(), edited.getId());
        return new CommandResult(withNotes(success, notes));
    }

    /** Returns another student whose name and contact match the edited details. */
    private static Optional<Student> findDuplicateStudent(Model model, StudentId editedId,
                                                           Name name, Contact contact) {
        return model.getTuiTracker().getStudentList().stream()
                .filter(student -> !student.getId().equals(editedId))
                .filter(student -> student.getName().isSameName(name)
                        && student.getContact().isSameContact(contact))
                .findFirst();
    }

    /** Returns another lesson whose subject, level and timeslot match the edited details. */
    private static Optional<Lesson> findDuplicateLesson(Model model, LessonId editedId, Subject subject,
                                                         Level level, Timeslot timeslot) {
        return model.getTuiTracker().getLessonList().stream()
                .filter(lesson -> !lesson.getId().equals(editedId))
                .filter(lesson -> lesson.getSubject().isSameSubject(subject)
                        && lesson.getLevel().equals(level)
                        && lesson.getTimeslot().equals(timeslot))
                .findFirst();
    }

    /** Returns another lesson whose timeslot overlaps the edited timeslot. */
    private static Optional<Lesson> findClashingLesson(Model model, LessonId editedId, Timeslot timeslot) {
        return model.getTuiTracker().getLessonList().stream()
                .filter(lesson -> !lesson.getId().equals(editedId))
                .filter(lesson -> lesson.getTimeslot().overlaps(timeslot))
                .findFirst();
    }

    /** Returns a note if the edited identity matches another student's name but not their contact. */
    private static Optional<String> noteForNamesake(Model model, StudentEdit edit, StudentId editedId,
                                                     Name name, Contact contact) {
        if (edit.name().isEmpty() && edit.contact().isEmpty()) {
            return Optional.empty();
        }
        return model.getTuiTracker().getStudentList().stream()
                .filter(student -> !student.getId().equals(editedId))
                .filter(student -> student.getName().isSameName(name))
                .filter(namesake -> !namesake.getContact().isSameContact(contact))
                .findFirst()
                .map(namesake -> String.format(MESSAGE_NOTE_SAME_NAME, namesake.getName(), namesake.getId()));
    }

    /** Returns one note for each enrolled lesson whose level differs from the edited student level. */
    private static List<String> notesForStudentLevelMismatches(Model model, StudentEdit edit, Student updated) {
        List<String> notes = new ArrayList<>();
        if (edit.level().isEmpty()) {
            return notes;
        }
        for (LessonId lessonId : model.getLessonIdsOf(updated.getId())) {
            Lesson lesson = findEnrolledLesson(model, lessonId);
            if (!lesson.getLevel().equals(updated.getLevel())) {
                notes.add(String.format(MESSAGE_NOTE_STUDENT_LEVEL_MISMATCH, updated.getName(), updated.getLevel(),
                        lesson.getId(), lesson.getLevel()));
            }
        }
        return notes;
    }

    /** Returns one note for each enrolled student whose level differs from the edited lesson level. */
    private static List<String> notesForLessonLevelMismatches(Model model, LessonEdit edit, Lesson updated) {
        List<String> notes = new ArrayList<>();
        if (edit.level().isEmpty()) {
            return notes;
        }
        for (Student student : model.getStudentsIn(updated.getId())) {
            if (!student.getLevel().equals(updated.getLevel())) {
                notes.add(String.format(MESSAGE_NOTE_LEVEL_MISMATCH, student.getName(), student.getLevel(),
                        updated.getLevel()));
            }
        }
        return notes;
    }

    /** Returns a note when the fee was explicitly changed to 0. */
    private static Optional<String> noteForFreeLesson(LessonEdit edit, Fee fee) {
        return edit.fee().isPresent() && fee.isFree()
                ? Optional.of(MESSAGE_NOTE_FREE_LESSON)
                : Optional.empty();
    }

    /** Returns true if the supplied level differs only in its stored spelling or letter case. */
    private static boolean hasDifferentLevelSpelling(Level original, Optional<Level> requested) {
        return requested.isPresent() && !requested.get().toString().equals(original.toString());
    }

    /** Returns the success message followed by each note on its own line. */
    private static String withNotes(String success, List<String> notes) {
        List<String> lines = new ArrayList<>();
        lines.add(success);
        lines.addAll(notes);
        return String.join("\n", lines);
    }

    private static Lesson findEnrolledLesson(Model model, LessonId id) {
        Optional<Lesson> lesson = model.findLesson(id);
        assert lesson.isPresent() : "Enrollment references a missing lesson";
        return lesson.orElseThrow();
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
            assert name.isPresent() || contact.isPresent() || level.isPresent()
                    : "A student edit must contain at least one field";
        }
    }

    /** Fields to change on one lesson. Empty fields retain their stored values. */
    public record LessonEdit(LessonId id, Optional<Subject> subject, Optional<Level> level,
                             Optional<DayOfWeek> day, Optional<LocalTime> start, Optional<LocalTime> end,
                             Optional<Fee> fee) implements EditTarget {
        /** Creates an edit request for a lesson. */
        public LessonEdit {
            requireAllNonNull(id, subject, level, day, start, end, fee);
            assert subject.isPresent() || level.isPresent() || day.isPresent() || start.isPresent()
                    || end.isPresent() || fee.isPresent()
                    : "A lesson edit must contain at least one field";
        }
    }
}
