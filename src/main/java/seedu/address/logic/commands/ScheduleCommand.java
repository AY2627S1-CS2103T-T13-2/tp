package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.ListView;
import seedu.address.model.Model;

/** Shows all recurring lessons in a Monday-to-Sunday weekly schedule. */
public class ScheduleCommand extends Command {

    public static final String COMMAND_WORD = "schedule";
    public static final String MESSAGE_USAGE = COMMAND_WORD;
    public static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter. Use: " + MESSAGE_USAGE;
    public static final String MESSAGE_SHOWING_SCHEDULE = "Showing weekly schedule.";
    public static final String MESSAGE_NO_LESSONS = "No lessons are scheduled for this week.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        boolean hasLessons = !model.getTuiTracker().getLessonList().isEmpty();
        model.setListView(ListView.SCHEDULE);
        return new CommandResult(hasLessons ? MESSAGE_SHOWING_SCHEDULE : MESSAGE_NO_LESSONS);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ScheduleCommand;
    }

    @Override
    public int hashCode() {
        return ScheduleCommand.class.hashCode();
    }
}
