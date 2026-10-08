package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import java.time.YearMonth;
import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Marks a student as paid through a specified month.
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " ID YYYY-MM";

    public static final String MESSAGE_SUCCESS = "Marked %1$s as paid through %2$s.";
    public static final String MESSAGE_ALREADY_PAID = "%1$s is already marked as paid through %2$s.";

    private final StudentId studentId;
    private final YearMonth paidThroughMonth;

    /**
     * Creates a command to mark {@code studentId} as paid through {@code paidThroughMonth}.
     */
    public MarkCommand(StudentId studentId, YearMonth paidThroughMonth) {
        this.studentId = requireNonNull(studentId);
        this.paidThroughMonth = requireNonNull(paidThroughMonth);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student student = model.findStudent(studentId)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, studentId)));

        if (student.getLastPaidMonth().filter(month -> !month.isBefore(paidThroughMonth)).isPresent()) {
            throw new CommandException(String.format(MESSAGE_ALREADY_PAID, student.getName(),
                    student.getLastPaidMonth().orElseThrow()));
        }

        Student updatedStudent = model.markPaid(studentId, paidThroughMonth);
        return new CommandResult(String.format(MESSAGE_SUCCESS, updatedStudent.getName(), paidThroughMonth));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof MarkCommand otherCommand
                && studentId.equals(otherCommand.studentId)
                && paidThroughMonth.equals(otherCommand.paidThroughMonth));
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, paidThroughMonth);
    }
}
