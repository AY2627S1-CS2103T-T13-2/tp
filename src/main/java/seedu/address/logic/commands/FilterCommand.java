package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

/**
 * Shows only the lessons of one student, or only the students of one lesson.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " student|lesson ID";

    public static final String MESSAGE_SHOWING_LESSONS = "Showing %1$d lesson(s) for student ID %2$s.";

    public static final String MESSAGE_SHOWING_STUDENTS = "Showing %1$d student(s) for lesson ID %2$s.";

    /** At most one of these is set, depending on whether the user filtered by student or by lesson. */
    private final StudentId studentId;
    private final LessonId lessonId;

    /**
     * Creates a command that shows only the lessons the given student is enrolled in.
     */
    public FilterCommand(StudentId studentId) {
        this.studentId = requireNonNull(studentId);
        this.lessonId = null;
    }

    /**
     * Creates a command that shows only the students enrolled in the given lesson.
     */
    public FilterCommand(LessonId lessonId) {
        this.studentId = null;
        this.lessonId = requireNonNull(lessonId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (studentId != null) {
            if (model.findStudent(studentId).isEmpty()) {
                throw new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, studentId));
            }

            model.filterLessonsByStudent(studentId);
            model.setListView(ListView.LESSONS);
            return new CommandResult(String.format(MESSAGE_SHOWING_LESSONS, model.getLessonList().size(),
                    studentId));
        }

        if (model.findLesson(lessonId).isEmpty()) {
            throw new CommandException(String.format(MESSAGE_LESSON_NOT_FOUND, lessonId));
        }

        model.filterStudentsByLesson(lessonId);
        model.setListView(ListView.STUDENTS);
        return new CommandResult(String.format(MESSAGE_SHOWING_STUDENTS, model.getStudentList().size(), lessonId));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FilterCommand otherFilterCommand)) {
            return false;
        }

        return Objects.equals(studentId, otherFilterCommand.studentId)
                && Objects.equals(lessonId, otherFilterCommand.lessonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, lessonId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("studentId", studentId)
                .add("lessonId", lessonId)
                .toString();
    }
}
