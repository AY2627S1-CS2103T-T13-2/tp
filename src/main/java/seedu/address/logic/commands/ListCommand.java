package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.ListView;
import seedu.address.model.Model;

/**
 * Shows all students or all lessons, and clears any active filter.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " [students|lessons]";

    public static final String MESSAGE_INVALID_VIEW = "Invalid view. Use: " + MESSAGE_USAGE;

    public static final String MESSAGE_VIEW_GIVEN_TWICE = "The view can only be specified once.";

    public static final String MESSAGE_CONFLICTING_VIEWS = "Choose either students or lessons, not both.";

    public static final String MESSAGE_LISTED_STUDENTS = "Listed %d students.";

    public static final String MESSAGE_LISTED_ONE_STUDENT = "Listed 1 student.";

    public static final String MESSAGE_LISTED_LESSONS = "Listed %d lessons.";

    public static final String MESSAGE_LISTED_ONE_LESSON = "Listed 1 lesson.";

    public static final String MESSAGE_NO_STUDENTS = "No students found. Use the add command to create one.";

    public static final String MESSAGE_NO_LESSONS =
            "No lessons found. Lessons are created through the add command.";

    private static final Logger logger = LogsCenter.getLogger(ListCommand.class);

    private final ListView view;

    /**
     * Creates a {@code ListCommand} that displays the specified view.
     */
    public ListCommand(ListView view) {
        requireNonNull(view);
        if (view == ListView.SCHEDULE) {
            throw new IllegalArgumentException("ListCommand cannot display the schedule view");
        }
        this.view = view;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        logger.fine("Executing list command for the " + view + " view");

        model.resetFilters();
        model.setScheduleStudentId(Optional.empty());
        model.setListView(view);

        return switch (view) {
            case STUDENTS -> listStudents(model);
            case LESSONS -> listLessons(model);
            case SCHEDULE -> throw new AssertionError("ListCommand must never display the schedule view");
        };
    }

    @Override
    public boolean isDataMutating() {
        return false;
    }

    private static CommandResult listStudents(Model model) {
        int studentCount = model.getStudentList().size();
        return createListResult(studentCount, "student", MESSAGE_NO_STUDENTS,
                MESSAGE_LISTED_ONE_STUDENT, MESSAGE_LISTED_STUDENTS);
    }

    private static CommandResult listLessons(Model model) {
        int lessonCount = model.getLessonList().size();
        return createListResult(lessonCount, "lesson", MESSAGE_NO_LESSONS,
                MESSAGE_LISTED_ONE_LESSON, MESSAGE_LISTED_LESSONS);
    }

    private static CommandResult createListResult(int count, String recordType, String emptyMessage,
                                                   String singularMessage, String pluralMessage) {
        String message;
        if (count == 0) {
            message = emptyMessage;
        } else if (count == 1) {
            message = singularMessage;
        } else {
            message = String.format(pluralMessage, count);
        }
        logger.info("Displayed the " + recordType + " list with " + count + " entries");
        return new CommandResult(message);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof ListCommand otherListCommand
                && view == otherListCommand.view;
    }

    @Override
    public int hashCode() {
        return Objects.hash(view);
    }
}
