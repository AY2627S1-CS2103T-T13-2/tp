package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
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

    public static final String MESSAGE_LISTED_STUDENTS = "Listed %d students.";

    public static final String MESSAGE_LISTED_LESSONS = "Listed %d lessons.";

    public static final String MESSAGE_NO_STUDENTS = "No students found. Use the add command to create one.";

    public static final String MESSAGE_NO_LESSONS = "No lessons found. Lessons are created through the add command.";

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
        String message = studentCount == 0
                ? MESSAGE_NO_STUDENTS
                : String.format(MESSAGE_LISTED_STUDENTS, studentCount);
        logger.info("Displayed the student list with " + studentCount + " entries");
        return new CommandResult(message);
    }

    private static CommandResult listLessons(Model model) {
        int lessonCount = model.getLessonList().size();
        String message = lessonCount == 0
                ? MESSAGE_NO_LESSONS
                : String.format(MESSAGE_LISTED_LESSONS, lessonCount);
        logger.info("Displayed the lesson list with " + lessonCount + " entries");
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
