package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/** Shows recurring lessons in a Monday-to-Sunday weekly schedule, optionally for one student. */
public class ScheduleCommand extends Command {

    public static final String COMMAND_WORD = "schedule";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " [student ID]";
    public static final String MESSAGE_INVALID_FORMAT = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE);
    public static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter. Use: " + MESSAGE_USAGE;
    public static final String MESSAGE_SHOWING_SCHEDULE = "Showing weekly schedule.";
    public static final String MESSAGE_NO_LESSONS = "No lessons are scheduled for this week.";
    public static final String MESSAGE_SHOWING_STUDENT_SCHEDULE = "Showing weekly schedule for %s (ID: %s).";
    public static final String MESSAGE_NO_STUDENT_LESSONS = "No lessons are scheduled for %s (ID: %s).";

    private final Optional<StudentId> studentId;

    /** Creates a command that shows every lesson. */
    public ScheduleCommand() {
        studentId = Optional.empty();
    }

    /** Creates a command that shows only the lessons attended by {@code studentId}. */
    public ScheduleCommand(StudentId studentId) {
        this.studentId = Optional.of(requireNonNull(studentId));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        String feedback;
        if (studentId.isPresent()) {
            StudentId id = studentId.get();
            Student student = model.findStudent(id)
                    .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, id)));
            boolean hasLessons = !model.getLessonIdsOf(id).isEmpty();
            String message = hasLessons ? MESSAGE_SHOWING_STUDENT_SCHEDULE : MESSAGE_NO_STUDENT_LESSONS;
            feedback = String.format(message, student.getName(), id);
        } else {
            boolean hasLessons = !model.getTuiTracker().getLessonList().isEmpty();
            feedback = hasLessons ? MESSAGE_SHOWING_SCHEDULE : MESSAGE_NO_LESSONS;
        }

        model.setScheduleStudentId(studentId);
        model.setListView(ListView.SCHEDULE);
        return new CommandResult(feedback);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof ScheduleCommand otherCommand
                && studentId.equals(otherCommand.studentId));
    }

    @Override
    public int hashCode() {
        return studentId.hashCode();
    }
}
