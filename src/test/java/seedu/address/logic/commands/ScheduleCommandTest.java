package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalClock.CLOCK;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import org.junit.jupiter.api.Test;

import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.TuiTracker;
import seedu.address.model.UserPrefs;

class ScheduleCommandTest {

    @Test
    void execute_showsScheduleWithoutClearingFilterOrChangingData() {
        Model model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        model.filterStudentsByLesson(MATHEMATICS.getId());
        TuiTracker dataBefore = new TuiTracker(model.getTuiTracker());

        CommandResult result = new ScheduleCommand().execute(model);

        assertEquals(ScheduleCommand.MESSAGE_SHOWING_SCHEDULE, result.getFeedbackToUser());
        assertEquals(ListView.SCHEDULE, model.getListView());
        assertEquals(1, model.getStudentList().size());
        assertEquals(dataBefore, new TuiTracker(model.getTuiTracker()));
    }

    @Test
    void execute_noLessons_reportsEmptySchedule() {
        Model model = new ModelManager(new TuiTracker(), new UserPrefs(), CLOCK);

        CommandResult result = new ScheduleCommand().execute(model);

        assertEquals(ScheduleCommand.MESSAGE_NO_LESSONS, result.getFeedbackToUser());
        assertEquals(ListView.SCHEDULE, model.getListView());
    }
}
