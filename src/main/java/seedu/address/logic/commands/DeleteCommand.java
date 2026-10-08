package seedu.address.logic.commands;

import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

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

    private final StudentId studentId;
    private final LessonId lessonId;

    /** Creates a command that deletes a student. */
    public DeleteCommand(StudentId studentId) {
        this.studentId = studentId;
        lessonId = null;
    }

    /** Creates a command that deletes a lesson. */
    public DeleteCommand(LessonId lessonId) {
        studentId = null;
        this.lessonId = lessonId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (studentId != null) {
            Student student = model.findStudent(studentId)
                    .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, studentId)));
            model.deleteStudent(studentId);
            return new CommandResult(String.format(MESSAGE_DELETE_STUDENT_SUCCESS, student.getName(), studentId));
        }

        Lesson lesson = model.findLesson(lessonId)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_LESSON_NOT_FOUND, lessonId)));
        model.deleteLesson(lessonId);
        return new CommandResult(String.format(MESSAGE_DELETE_LESSON_SUCCESS, lesson.getSubject() + " | "
                + lesson.getLevel() + " | " + lesson.getTimeslot(), lessonId));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteCommand otherCommand)) {
            return false;
        }
        return java.util.Objects.equals(studentId, otherCommand.studentId)
                && java.util.Objects.equals(lessonId, otherCommand.lessonId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(studentId, lessonId);
    }
}
