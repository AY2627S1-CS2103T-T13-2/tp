package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Marks a student as unpaid for the current month.
 */
public class UnmarkCommand extends Command {

    public static final String COMMAND_WORD = "unmark";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " ID";
    public static final String MESSAGE_SUCCESS = "Marked %1$s as unpaid for the current month.";
    public static final String MESSAGE_ALREADY_UNPAID = "%1$s is already unpaid for the current month.";

    private final StudentId studentId;

    public UnmarkCommand(StudentId studentId) {
        this.studentId = requireNonNull(studentId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student student = model.findStudent(studentId)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, studentId)));

        if (!model.isPaid(studentId)) {
            throw new CommandException(String.format(MESSAGE_ALREADY_UNPAID, student.getName()));
        }

        Student updatedStudent = model.unmarkPaid(studentId);
        return new CommandResult(String.format(MESSAGE_SUCCESS, updatedStudent.getName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof UnmarkCommand otherCommand
                && studentId.equals(otherCommand.studentId));
    }
}
