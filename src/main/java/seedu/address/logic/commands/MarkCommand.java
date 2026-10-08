package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Marks a student as paid for the current month.
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " ID";

    public static final String MESSAGE_SUCCESS = "Marked %1$s as paid for the current month.";
    public static final String MESSAGE_ALREADY_PAID = "%1$s is already marked as paid for the current month.";

    private final StudentId studentId;

    /**
     * Creates a command to mark {@code studentId} as paid for the current month.
     */
    public MarkCommand(StudentId studentId) {
        this.studentId = requireNonNull(studentId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student student = model.findStudent(studentId)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, studentId)));

        if (model.isPaid(studentId)) {
            throw new CommandException(String.format(MESSAGE_ALREADY_PAID, student.getName()));
        }

        Student updatedStudent = model.markPaid(studentId);
        return new CommandResult(String.format(MESSAGE_SUCCESS, updatedStudent.getName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof MarkCommand otherCommand
                && studentId.equals(otherCommand.studentId));
    }
}
