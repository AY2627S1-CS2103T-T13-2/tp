package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_NOT_FOUND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalClock.CLOCK;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.TuiTracker;
import seedu.address.model.UserPrefs;
import seedu.address.model.common.Level;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

class ScheduleCommandTest {

    @Test
    void execute_showsScheduleWithoutClearingFilterOrChangingData() throws Exception {
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
    void execute_noLessons_reportsEmptySchedule() throws Exception {
        Model model = new ModelManager(new TuiTracker(), new UserPrefs(), CLOCK);

        CommandResult result = new ScheduleCommand().execute(model);

        assertEquals(ScheduleCommand.MESSAGE_NO_LESSONS, result.getFeedbackToUser());
        assertEquals(ListView.SCHEDULE, model.getListView());
    }

    @Test
    void execute_studentExists_selectsStudentWithoutClearingFilterOrChangingData() throws Exception {
        Model model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        model.filterStudentsByLesson(MATHEMATICS.getId());
        TuiTracker dataBefore = new TuiTracker(model.getTuiTracker());

        CommandResult result = new ScheduleCommand(ALICE.getId()).execute(model);

        assertEquals(String.format(ScheduleCommand.MESSAGE_SHOWING_STUDENT_SCHEDULE,
                ALICE.getName(), ALICE.getId()), result.getFeedbackToUser());
        assertEquals(Optional.of(ALICE.getId()), model.getScheduleStudentId());
        assertEquals(ListView.SCHEDULE, model.getListView());
        assertEquals(1, model.getStudentList().size());
        assertEquals(dataBefore, new TuiTracker(model.getTuiTracker()));
    }

    @Test
    void execute_studentWithoutLessons_reportsEmptySchedule() throws Exception {
        Model model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        Student student = model.addStudent(new Name("Daniel Koh"), new Contact("92223333"),
                new Level("Primary 6"));

        CommandResult result = new ScheduleCommand(student.getId()).execute(model);

        assertEquals(String.format(ScheduleCommand.MESSAGE_NO_STUDENT_LESSONS,
                student.getName(), student.getId()), result.getFeedbackToUser());
        assertEquals(Optional.of(student.getId()), model.getScheduleStudentId());
    }

    @Test
    void execute_missingStudent_leavesViewAndSelectionUnchanged() throws Exception {
        Model model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        new ScheduleCommand(ALICE.getId()).execute(model);
        StudentId missingId = new StudentId(999);

        assertCommandFailure(new ScheduleCommand(missingId), model,
                String.format(MESSAGE_STUDENT_NOT_FOUND, missingId));
        assertEquals(Optional.of(ALICE.getId()), model.getScheduleStudentId());
        assertEquals(ListView.SCHEDULE, model.getListView());
    }

    @Test
    void execute_plainSchedule_clearsStudentSelection() throws Exception {
        Model model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
        new ScheduleCommand(ALICE.getId()).execute(model);

        CommandResult result = new ScheduleCommand().execute(model);

        assertEquals(ScheduleCommand.MESSAGE_SHOWING_SCHEDULE, result.getFeedbackToUser());
        assertEquals(Optional.empty(), model.getScheduleStudentId());
    }
}
