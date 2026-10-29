package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.Messages.MESSAGE_LESSON_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.StudentId;

/**
 * Shows only the lessons of one student, only the students of one lesson, only the lessons on one day,
 * only the students who are paid or unpaid for the current month, only the students at a given level,
 * only the lessons with a given fee, or only the lessons with a given subject, level, or both.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " student|lesson ID or " + COMMAND_WORD + " day DAY or "
            + COMMAND_WORD + " paid|unpaid or " + COMMAND_WORD + " [l/SUBJECT] [llv/LESSON_LEVEL] or "
            + COMMAND_WORD + " slv/STUDENT_LEVEL or " + COMMAND_WORD + " f/FEE";

    public static final String MESSAGE_SHOWING_LESSONS = "Showing %1$d lesson(s) for student ID %2$s.";

    public static final String MESSAGE_SHOWING_STUDENTS = "Showing %1$d student(s) for lesson ID %2$s.";

    public static final String MESSAGE_SHOWING_LESSONS_ON_DAY = "Showing %1$d lesson(s) on %2$s.";

    public static final String MESSAGE_SHOWING_LESSONS_MATCHING = "Showing %1$d lesson(s) with %2$s.";

    public static final String MESSAGE_SHOWING_STUDENTS_BY_PAID_STATUS =
            "Showing %1$d student(s) who are %2$s for the current month.";

    public static final String MESSAGE_SHOWING_STUDENTS_BY_LEVEL = "Showing %1$d student(s) at level %2$s.";

    public static final String MESSAGE_SHOWING_LESSONS_BY_FEE = "Showing %1$d lesson(s) with fee %2$s.";

    /**
     * Only one kind of filter is set: the student, the lesson, the day, the paid status, the student level,
     * the fee, or the subject and level. When filtering by subject and level, either one or both may be set.
     */
    private final StudentId studentId;
    private final LessonId lessonId;
    private final DayOfWeek day;
    private final Subject subject;
    private final Level level;
    private final Boolean paidStatus;
    private final Level studentLevel;
    private final Fee fee;

    /**
     * Creates a command that shows only the lessons the given student is enrolled in.
     */
    public FilterCommand(StudentId studentId) {
        this.studentId = requireNonNull(studentId);
        this.lessonId = null;
        this.day = null;
        this.subject = null;
        this.level = null;
        this.paidStatus = null;
        this.studentLevel = null;
        this.fee = null;
    }

    /**
     * Creates a command that shows only the students enrolled in the given lesson.
     */
    public FilterCommand(LessonId lessonId) {
        this.studentId = null;
        this.lessonId = requireNonNull(lessonId);
        this.day = null;
        this.subject = null;
        this.level = null;
        this.paidStatus = null;
        this.studentLevel = null;
        this.fee = null;
    }

    /**
     * Creates a command that shows only the lessons on the given day.
     */
    public FilterCommand(DayOfWeek day) {
        this.studentId = null;
        this.lessonId = null;
        this.day = requireNonNull(day);
        this.subject = null;
        this.level = null;
        this.paidStatus = null;
        this.studentLevel = null;
        this.fee = null;
    }

    /**
     * Creates a command that shows only the lessons with the given subject and level.
     * Either one may be null to leave it out, but not both.
     */
    public FilterCommand(Subject subject, Level level) {
        checkArgument(subject != null || level != null, "At least one of subject and level must be given.");
        this.studentId = null;
        this.lessonId = null;
        this.day = null;
        this.subject = subject;
        this.level = level;
        this.paidStatus = null;
        this.studentLevel = null;
        this.fee = null;
    }

    /**
     * Creates a command that shows only the students who are (or are not) paid for the current month.
     */
    public FilterCommand(boolean paidStatus) {
        this.studentId = null;
        this.lessonId = null;
        this.day = null;
        this.subject = null;
        this.level = null;
        this.paidStatus = paidStatus;
        this.studentLevel = null;
        this.fee = null;
    }

    /**
     * Creates a command that shows only the students at the given level.
     */
    public FilterCommand(Level studentLevel) {
        this.studentId = null;
        this.lessonId = null;
        this.day = null;
        this.subject = null;
        this.level = null;
        this.paidStatus = null;
        this.studentLevel = requireNonNull(studentLevel);
        this.fee = null;
    }

    /**
     * Creates a command that shows only the lessons with the given fee.
     */
    public FilterCommand(Fee fee) {
        this.studentId = null;
        this.lessonId = null;
        this.day = null;
        this.subject = null;
        this.level = null;
        this.paidStatus = null;
        this.studentLevel = null;
        this.fee = requireNonNull(fee);
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

        if (lessonId != null) {
            if (model.findLesson(lessonId).isEmpty()) {
                throw new CommandException(String.format(MESSAGE_LESSON_NOT_FOUND, lessonId));
            }

            model.filterStudentsByLesson(lessonId);
            model.setListView(ListView.STUDENTS);
            return new CommandResult(String.format(MESSAGE_SHOWING_STUDENTS, model.getStudentList().size(),
                    lessonId));
        }

        if (day != null) {
            model.filterLessonsByDay(day);
            model.setListView(ListView.LESSONS);
            return new CommandResult(String.format(MESSAGE_SHOWING_LESSONS_ON_DAY, model.getLessonList().size(),
                    Timeslot.dayName(day)));
        }

        if (paidStatus != null) {
            model.filterStudentsByPaidStatus(paidStatus);
            model.setListView(ListView.STUDENTS);
            return new CommandResult(String.format(MESSAGE_SHOWING_STUDENTS_BY_PAID_STATUS,
                    model.getStudentList().size(), paidStatus ? "paid" : "unpaid"));
        }

        if (studentLevel != null) {
            model.filterStudentsByLevel(studentLevel);
            model.setListView(ListView.STUDENTS);
            return new CommandResult(String.format(MESSAGE_SHOWING_STUDENTS_BY_LEVEL,
                    model.getStudentList().size(), studentLevel));
        }

        if (fee != null) {
            model.filterLessonsByFee(fee);
            model.setListView(ListView.LESSONS);
            return new CommandResult(String.format(MESSAGE_SHOWING_LESSONS_BY_FEE, model.getLessonList().size(),
                    fee));
        }

        model.filterLessonsBySubjectAndLevel(subject, level);
        model.setListView(ListView.LESSONS);
        return new CommandResult(String.format(MESSAGE_SHOWING_LESSONS_MATCHING, model.getLessonList().size(),
                describeSubjectAndLevel()));
    }

    /**
     * Returns the subject and level being filtered by, e.g. "subject Mathematics and level Primary 6".
     */
    private String describeSubjectAndLevel() {
        List<String> parts = new ArrayList<>();
        if (subject != null) {
            parts.add("subject " + subject);
        }
        if (level != null) {
            parts.add("level " + level);
        }
        return String.join(" and ", parts);
    }

    @Override
    public boolean isDataMutating() {
        return false;
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
                && Objects.equals(lessonId, otherFilterCommand.lessonId)
                && Objects.equals(day, otherFilterCommand.day)
                && Objects.equals(subject, otherFilterCommand.subject)
                && Objects.equals(level, otherFilterCommand.level)
                && Objects.equals(paidStatus, otherFilterCommand.paidStatus)
                && Objects.equals(studentLevel, otherFilterCommand.studentLevel)
                && Objects.equals(fee, otherFilterCommand.fee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, lessonId, day, subject, level, paidStatus, studentLevel, fee);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("studentId", studentId)
                .add("lessonId", lessonId)
                .add("day", day)
                .add("subject", subject)
                .add("level", level)
                .add("paidStatus", paidStatus)
                .add("studentLevel", studentLevel)
                .add("fee", fee)
                .toString();
    }
}
