package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/** Deletes one student or one lesson by ID, together with its enrollments. */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " student|lesson ID";
    public static final String MESSAGE_DELETE_STUDENT_SUCCESS = "Deleted student: %s (ID: %s)";
    public static final String MESSAGE_DELETE_LESSON_SUCCESS = "Deleted lesson: %s (ID: %s)";

    /** Exactly one ID is set, according to the requested record kind. */
    private final StudentId studentId;
    private final LessonId lessonId;

    /** Creates a command that deletes a student. */
    public DeleteCommand(StudentId studentId) {
        this.studentId = requireNonNull(studentId);
        lessonId = null;
    }

    /** Creates a command that deletes a lesson. */
    public DeleteCommand(LessonId lessonId) {
        studentId = null;
        this.lessonId = requireNonNull(lessonId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        assert (studentId == null) != (lessonId == null) : "Exactly one delete ID must be set";

        if (studentId != null) {
            return deleteStudent(model);
        }
        return deleteLesson(model);
    }

    private CommandResult deleteStudent(Model model) throws CommandException {
        Student student = model.findStudent(studentId)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, studentId)));
        Student deleted = model.deleteStudent(studentId);
        assert student.equals(deleted) : "The deleted student must match the requested student";
        return new CommandResult(String.format(MESSAGE_DELETE_STUDENT_SUCCESS, deleted.getName(), deleted.getId()));
    }

    private CommandResult deleteLesson(Model model) throws CommandException {
        Lesson lesson = model.findLesson(lessonId)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_LESSON_NOT_FOUND, lessonId)));
        Lesson deleted = model.deleteLesson(lessonId);
        assert lesson.equals(deleted) : "The deleted lesson must match the requested lesson";
        String description = deleted.getSubject() + " | " + deleted.getLevel() + " | " + deleted.getTimeslot();
        return new CommandResult(String.format(MESSAGE_DELETE_LESSON_SUCCESS, description, deleted.getId()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteCommand otherCommand)) {
            return false;
        }
        return Objects.equals(studentId, otherCommand.studentId)
                && Objects.equals(lessonId, otherCommand.lessonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, lessonId);
    }
}
