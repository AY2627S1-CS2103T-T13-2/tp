package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Marks one or more students, or all students in a lesson, as paid for the current month.
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " ID [MORE_IDS]... or "
            + COMMAND_WORD + " lesson LESSON_ID";

    public static final String MESSAGE_SUCCESS = "Marked %1$s as paid for the current month.";
    public static final String MESSAGE_SUCCESS_WITH_ALREADY_PAID =
            "Marked %1$s as paid for the current month. (Already paid: %2$s)";
    public static final String MESSAGE_ALREADY_PAID = "%1$s is already marked as paid for the current month.";
    public static final String MESSAGE_ALL_ALREADY_PAID =
            "All specified students are already marked as paid for the current month.";
    public static final String MESSAGE_NO_STUDENTS_IN_LESSON = "No students are enrolled in Lesson %1$s.";
    public static final String MESSAGE_LESSON_ALL_ALREADY_PAID =
            "All students in Lesson %1$s are already marked as paid for the current month.";

    private final List<StudentId> targetStudentIds;
    private final LessonId targetLessonId;

    /**
     * Creates a command to mark a single {@code studentId} as paid for the current month.
     */
    public MarkCommand(StudentId studentId) {
        this(Collections.singletonList(requireNonNull(studentId)));
    }

    /**
     * Creates a command to mark multiple {@code targetStudentIds} as paid for the current month.
     */
    public MarkCommand(List<StudentId> targetStudentIds) {
        requireNonNull(targetStudentIds);
        this.targetStudentIds = List.copyOf(new LinkedHashSet<>(targetStudentIds));
        this.targetLessonId = null;
    }

    /**
     * Creates a command to mark all students enrolled in {@code targetLessonId} as paid for the current month.
     */
    public MarkCommand(LessonId targetLessonId) {
        this.targetStudentIds = null;
        this.targetLessonId = requireNonNull(targetLessonId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (targetLessonId != null) {
            Lesson lesson = model.findLesson(targetLessonId)
                    .orElseThrow(() -> new CommandException(String.format(MESSAGE_LESSON_NOT_FOUND, targetLessonId)));
            List<Student> students = model.getStudentsIn(targetLessonId);
            if (students.isEmpty()) {
                throw new CommandException(String.format(MESSAGE_NO_STUDENTS_IN_LESSON, targetLessonId));
            }
            List<Student> unpaid = new ArrayList<>();
            List<Student> alreadyPaid = new ArrayList<>();
            for (Student student : students) {
                if (model.isPaid(student.getId())) {
                    alreadyPaid.add(student);
                } else {
                    unpaid.add(student);
                }
            }
            if (unpaid.isEmpty()) {
                throw new CommandException(String.format(MESSAGE_LESSON_ALL_ALREADY_PAID, targetLessonId));
            }
            for (Student s : unpaid) {
                model.markPaid(s.getId());
            }
            String markedNames = unpaid.stream().map(s -> s.getName().fullName).collect(Collectors.joining(", "));
            if (alreadyPaid.isEmpty()) {
                return new CommandResult(String.format(MESSAGE_SUCCESS, markedNames));
            } else {
                String alreadyPaidNames = alreadyPaid.stream()
                        .map(s -> s.getName().fullName).collect(Collectors.joining(", "));
                return new CommandResult(String.format(MESSAGE_SUCCESS_WITH_ALREADY_PAID,
                        markedNames, alreadyPaidNames));
            }
        }

        List<Student> students = new ArrayList<>();
        for (StudentId id : targetStudentIds) {
            Student student = model.findStudent(id)
                    .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, id)));
            students.add(student);
        }

        if (targetStudentIds.size() == 1) {
            Student student = students.get(0);
            if (model.isPaid(student.getId())) {
                throw new CommandException(String.format(MESSAGE_ALREADY_PAID, student.getName()));
            }
            Student updatedStudent = model.markPaid(student.getId());
            return new CommandResult(String.format(MESSAGE_SUCCESS, updatedStudent.getName()));
        }

        List<Student> unpaid = new ArrayList<>();
        List<Student> alreadyPaid = new ArrayList<>();
        for (Student student : students) {
            if (model.isPaid(student.getId())) {
                alreadyPaid.add(student);
            } else {
                unpaid.add(student);
            }
        }
        if (unpaid.isEmpty()) {
            throw new CommandException(MESSAGE_ALL_ALREADY_PAID);
        }
        for (Student s : unpaid) {
            model.markPaid(s.getId());
        }
        String markedNames = unpaid.stream().map(s -> s.getName().fullName).collect(Collectors.joining(", "));
        if (alreadyPaid.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_SUCCESS, markedNames));
        } else {
            String alreadyPaidNames = alreadyPaid.stream()
                    .map(s -> s.getName().fullName).collect(Collectors.joining(", "));
            return new CommandResult(String.format(MESSAGE_SUCCESS_WITH_ALREADY_PAID,
                    markedNames, alreadyPaidNames));
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof MarkCommand otherCommand
                && Objects.equals(targetStudentIds, otherCommand.targetStudentIds)
                && Objects.equals(targetLessonId, otherCommand.targetLessonId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetStudentIds, targetLessonId);
    }
}
