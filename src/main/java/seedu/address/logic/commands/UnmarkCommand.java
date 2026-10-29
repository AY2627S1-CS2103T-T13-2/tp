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
 * Marks one or more students, or all students in a lesson, as unpaid for the current month.
 */
public class UnmarkCommand extends Command {

    public static final String COMMAND_WORD = "unmark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " ID [MORE_IDS]... or "
            + COMMAND_WORD + " lesson LESSON_ID";

    public static final String MESSAGE_SUCCESS = "Marked %1$s as unpaid for the current month.";
    public static final String MESSAGE_SUCCESS_WITH_ALREADY_UNPAID =
            "Marked %1$s as unpaid for the current month. (Already unpaid: %2$s)";
    public static final String MESSAGE_ALREADY_UNPAID = "%1$s is already unpaid for the current month.";
    public static final String MESSAGE_ALL_ALREADY_UNPAID =
            "All specified students are already unpaid for the current month.";
    public static final String MESSAGE_NO_STUDENTS_IN_LESSON = "No students are enrolled in Lesson %1$s.";
    public static final String MESSAGE_LESSON_ALL_ALREADY_UNPAID =
            "All students in Lesson %1$s are already unpaid for the current month.";

    private final List<StudentId> targetStudentIds;
    private final LessonId targetLessonId;

    /**
     * Creates a command to mark a single {@code studentId} as unpaid for the current month.
     */
    public UnmarkCommand(StudentId studentId) {
        this(Collections.singletonList(requireNonNull(studentId)));
    }

    /**
     * Creates a command to mark multiple {@code targetStudentIds} as unpaid for the current month.
     */
    public UnmarkCommand(List<StudentId> targetStudentIds) {
        requireNonNull(targetStudentIds);
        this.targetStudentIds = List.copyOf(new LinkedHashSet<>(targetStudentIds));
        this.targetLessonId = null;
    }

    /**
     * Creates a command to mark all students enrolled in {@code targetLessonId} as unpaid for the current month.
     */
    public UnmarkCommand(LessonId targetLessonId) {
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
            List<Student> paid = new ArrayList<>();
            List<Student> alreadyUnpaid = new ArrayList<>();
            for (Student student : students) {
                if (model.isPaid(student.getId())) {
                    paid.add(student);
                } else {
                    alreadyUnpaid.add(student);
                }
            }
            if (paid.isEmpty()) {
                throw new CommandException(String.format(MESSAGE_LESSON_ALL_ALREADY_UNPAID, targetLessonId));
            }
            for (Student s : paid) {
                model.unmarkPaid(s.getId());
            }
            String unmarkedNames = paid.stream().map(s -> s.getName().fullName).collect(Collectors.joining(", "));
            if (alreadyUnpaid.isEmpty()) {
                return new CommandResult(String.format(MESSAGE_SUCCESS, unmarkedNames));
            } else {
                String alreadyUnpaidNames = alreadyUnpaid.stream()
                        .map(s -> s.getName().fullName).collect(Collectors.joining(", "));
                return new CommandResult(String.format(MESSAGE_SUCCESS_WITH_ALREADY_UNPAID,
                        unmarkedNames, alreadyUnpaidNames));
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
            if (!model.isPaid(student.getId())) {
                throw new CommandException(String.format(MESSAGE_ALREADY_UNPAID, student.getName()));
            }
            Student updatedStudent = model.unmarkPaid(student.getId());
            return new CommandResult(String.format(MESSAGE_SUCCESS, updatedStudent.getName()));
        }

        List<Student> paid = new ArrayList<>();
        List<Student> alreadyUnpaid = new ArrayList<>();
        for (Student student : students) {
            if (model.isPaid(student.getId())) {
                paid.add(student);
            } else {
                alreadyUnpaid.add(student);
            }
        }
        if (paid.isEmpty()) {
            throw new CommandException(MESSAGE_ALL_ALREADY_UNPAID);
        }
        for (Student s : paid) {
            model.unmarkPaid(s.getId());
        }
        String unmarkedNames = paid.stream().map(s -> s.getName().fullName).collect(Collectors.joining(", "));
        if (alreadyUnpaid.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_SUCCESS, unmarkedNames));
        } else {
            String alreadyUnpaidNames = alreadyUnpaid.stream()
                    .map(s -> s.getName().fullName).collect(Collectors.joining(", "));
            return new CommandResult(String.format(MESSAGE_SUCCESS_WITH_ALREADY_UNPAID,
                    unmarkedNames, alreadyUnpaidNames));
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof UnmarkCommand otherCommand
                && Objects.equals(targetStudentIds, otherCommand.targetStudentIds)
                && Objects.equals(targetLessonId, otherCommand.targetLessonId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetStudentIds, targetLessonId);
    }
}
