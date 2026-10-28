package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.EditCommand.MESSAGE_NO_CHANGES;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalClock.CLOCK;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalStudents.CHLOE;
import static seedu.address.testutil.TypicalStudents.DANIEL;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyTuiTracker;
import seedu.address.model.TuiTracker;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;
import seedu.address.storage.JsonTuiTrackerStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager(getTypicalTuiTracker(), new UserPrefs(), CLOCK);
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonTuiTrackerStorage tuiTrackerStorage = new JsonTuiTrackerStorage(temporaryFolder.resolve("tuitracker.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"));
        logic = new LogicManager(model, new StorageManager(tuiTrackerStorage, userPrefsStorage));
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        assertParseException("uicfhmowqewca", MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_blankCommand_throwsParseException() {
        assertParseException("", String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.COMMAND_WORD));
    }

    @Test
    public void execute_commandWithPersonalDetails_logsNoNamesOrContactNumbers() throws Throwable {
        List<String> logged = logsDuring(() -> logic.execute("add s/Zara Quinn c/91112222 slv/Primary 6 l/1"));

        assertNoPersonalData(logged);
    }

    @Test
    public void execute_rejectedCommandWithPersonalDetails_logsNoNamesOrContactNumbers() throws Throwable {
        // these error messages repeat what was typed: the name read as a new student, and a number that is not an ID
        List<String> logged = logsDuring(() -> {
            assertThrows(ParseException.class, () -> logic.execute("add s/Zara Quinn slv/Primary 6 l/1"));
            assertThrows(ParseException.class, () -> logic.execute("add s/+6591112222 l/1"));
        });

        assertNoPersonalData(logged);
    }

    /**
     * Runs {@code action} and returns every message logged meanwhile, including those at FINE level.
     */
    private static List<String> logsDuring(Executable action) throws Throwable {
        List<String> logged = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                logged.add(record.getMessage());
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        };
        Logger baseLogger = LogsCenter.getLogger(LogicManager.class).getParent();
        java.util.logging.Level originalLevel = baseLogger.getLevel();
        baseLogger.setLevel(java.util.logging.Level.FINE);
        baseLogger.addHandler(handler);
        try {
            action.execute();
        } finally {
            baseLogger.removeHandler(handler);
            baseLogger.setLevel(originalLevel);
        }
        return logged;
    }

    private static void assertNoPersonalData(List<String> logged) {
        assertFalse(logged.isEmpty());
        for (String message : logged) {
            assertFalse(message.contains("Zara") || message.contains("91112222"),
                    "Personal data was logged: " + message);
        }
    }

    @Test
    public void execute_mutatingCommandThatSucceeds_savesTheData() throws Exception {
        logic.execute("mark 1");
        assertTrue(temporaryFolder.resolve("tuitracker.json").toFile().exists());
    }

    @Test
    public void execute_studentAndLessonAddedApart_persistAndCanBeEnrolledAfterRestart() throws Exception {
        logic.execute("add s/Nora Lim c/90000000 slv/Primary 6");
        logic.execute("add l/Mathematics llv/Primary 6 d/Friday st/15:00 et/16:00 f/50");

        // restart from what was saved
        JsonTuiTrackerStorage storage = new JsonTuiTrackerStorage(temporaryFolder.resolve("tuitracker.json"));
        ReadOnlyTuiTracker saved = storage.readTuiTracker().orElseThrow();
        assertTrue(saved.getStudentList().stream().anyMatch(student -> student.getName().fullName.equals("Nora Lim")
                && student.getId().value == 5));
        assertTrue(saved.getLessonList().stream().anyMatch(lesson -> lesson.getId().value == 6));
        assertTrue(saved.getEnrollmentList().stream().noneMatch(enrollment -> enrollment.getStudentId().value == 5
                || enrollment.getLessonId().value == 6));
        Model restarted = new ModelManager(saved, new UserPrefs(), CLOCK);
        Logic restartedLogic = new LogicManager(restarted, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));

        CommandResult result = restartedLogic.execute("add s/5 l/6");

        assertEquals("Enrolled Nora Lim [Student ID: 5] in Mathematics [Lesson ID: 6]. Payment status: Unpaid.",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_existingStudentWithoutLesson_logsNoName() throws Throwable {
        // the error message names the student, so that the tutor can check it is the right one
        logic.execute("add s/Zara Quinn c/91112222 slv/Primary 6");
        List<String> logged = logsDuring(() -> assertThrows(CommandException.class, () -> logic.execute("add s/5")));

        assertNoPersonalData(logged);
    }

    @Test
    public void execute_helpWhenStorageFails_stillShowsHelp() throws Exception {
        JsonTuiTrackerStorage failingStorage = new JsonTuiTrackerStorage(temporaryFolder.resolve("failing.json")) {
            @Override
            public void saveTuiTracker(ReadOnlyTuiTracker tuiTracker, Path filePath) throws IOException {
                throw DUMMY_IO_EXCEPTION;
            }
        };
        Logic failingLogic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));

        CommandResult result = failingLogic.execute("help");

        assertEquals(HelpCommand.SHOWING_HELP_MESSAGE, result.getFeedbackToUser());
    }

    @Test
    public void execute_sortWhenStorageFails_stillSortsWithoutSaving() throws Exception {
        Logic failingLogic = createLogicWithFailingStorage();

        CommandResult result = failingLogic.execute("sort student name desc");

        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS, "students", "name", "descending"),
                result.getFeedbackToUser());
        assertEquals(ListView.STUDENTS, model.listViewProperty().get());
        assertEquals(CHLOE, model.getStudentList().get(0));
    }

    @Test
    public void execute_noChangeEditWhenStorageFails_skipsSaveAndReportsNoChanges() throws Exception {
        Logic failingLogic = createLogicWithFailingStorage();

        CommandResult result = failingLogic.execute("edit student 1 s/Alice Tan");

        assertEquals(MESSAGE_NO_CHANGES, result.getFeedbackToUser());
    }

    @Test
    public void execute_caseOnlyLevelEditWhenStorageFails_attemptsSaveAndRollsBack() {
        Logic failingLogic = createLogicWithFailingStorage();

        CommandException thrown = Assertions.assertThrows(CommandException.class, () ->
                failingLogic.execute("edit student 1 slv/primary 6"));

        assertEquals(String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()),
                thrown.getMessage());
        assertEquals("Primary 6", model.findStudent(ALICE.getId()).orElseThrow().getLevel().toString());
    }

    //// a command whose changes cannot be saved is undone

    @Test
    public void execute_ioExceptionWhileSaving_undoesTheCommandAndReportsTheError() {
        Logic failingLogic = logicThatChangesDataThenFailsToSave(DUMMY_IO_EXCEPTION, null);
        TuiTracker before = new TuiTracker(model.getTuiTracker());

        CommandException thrown = Assertions.assertThrows(CommandException.class, () ->
                failingLogic.execute("anything"));

        assertEquals(String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()),
                thrown.getMessage());
        assertEquals(before, new TuiTracker(model.getTuiTracker()));
    }

    @Test
    public void execute_accessDeniedWhileSaving_undoesTheCommandAndReportsThePermissionError() {
        Logic failingLogic = logicThatChangesDataThenFailsToSave(DUMMY_AD_EXCEPTION, null);
        TuiTracker before = new TuiTracker(model.getTuiTracker());

        CommandException thrown = Assertions.assertThrows(CommandException.class, () ->
                failingLogic.execute("anything"));

        assertEquals(String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()),
                thrown.getMessage());
        assertEquals(before, new TuiTracker(model.getTuiTracker()));
    }

    @Test
    public void execute_failedSave_doesNotUseUpAnId() {
        Logic failingLogic = logicThatChangesDataThenFailsToSave(DUMMY_IO_EXCEPTION, null);
        int nextStudentIdBefore = model.getTuiTracker().getNextStudentId();

        Assertions.assertThrows(CommandException.class, () -> failingLogic.execute("anything"));

        assertEquals(nextStudentIdBefore, model.getTuiTracker().getNextStudentId());
    }

    @Test
    public void execute_failedSave_usesTheCommandsOwnMessageIfItHasOne() {
        String ownMessage = "The payment status could not be saved. No changes were made.";
        Logic failingLogic = logicThatChangesDataThenFailsToSave(DUMMY_IO_EXCEPTION, ownMessage);

        CommandException thrown = Assertions.assertThrows(CommandException.class, () ->
                failingLogic.execute("anything"));

        assertEquals(ownMessage, thrown.getMessage());
    }

    @Test
    public void execute_markWhenSaveFails_reportsGenericErrorAndRollsBack() {
        JsonTuiTrackerStorage failingStorage = new JsonTuiTrackerStorage(temporaryFolder.resolve("failing.json")) {
            @Override
            public void saveTuiTracker(ReadOnlyTuiTracker tuiTracker, Path filePath) throws IOException {
                throw DUMMY_IO_EXCEPTION;
            }
        };
        Logic failingLogic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));

        CommandException thrown = Assertions.assertThrows(
                CommandException.class, () -> failingLogic.execute("mark 1"));

        assertEquals(String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()),
                thrown.getMessage());
        assertFalse(model.isPaid(ALICE.getId()));
    }

    @Test
    public void execute_deleteScheduleStudentWhenSaveFails_restoresSelection() {
        model.setScheduleStudentId(Optional.of(ALICE.getId()));
        model.setListView(ListView.SCHEDULE);
        JsonTuiTrackerStorage failingStorage = new JsonTuiTrackerStorage(temporaryFolder.resolve("failing.json")) {
            @Override
            public void saveTuiTracker(ReadOnlyTuiTracker tuiTracker, Path filePath) throws IOException {
                throw DUMMY_IO_EXCEPTION;
            }
        };
        Logic failingLogic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));

        Assertions.assertThrows(CommandException.class, () -> failingLogic.execute("delete student 1"));

        assertTrue(model.findStudent(ALICE.getId()).isPresent());
        assertEquals(Optional.of(ALICE.getId()), model.getScheduleStudentId());
        assertEquals(ListView.SCHEDULE, model.getListView());
    }

    //// delegation to the model

    @Test
    public void listsAndDerivedData_comeFromTheModel() {
        assertEquals(model.getStudentList(), logic.getStudentList());
        assertEquals(model.getLessonList(), logic.getLessonList());
        assertEquals(Arrays.asList(MATHEMATICS.getId(), seedu.address.testutil.TypicalLessons.SCIENCE_P6.getId()),
                logic.getLessonIdsOf(ALICE.getId()));
        assertEquals(Arrays.asList(ALICE), logic.getStudentsIn(MATHEMATICS.getId()));
        assertFalse(logic.isPaid(ALICE.getId()));
        assertTrue(logic.isPaid(BRYAN.getId()));
    }

    @Test
    public void getAllLessons_ignoresActiveFilterForSchedule() {
        model.filterLessonsByStudent(ALICE.getId());

        assertEquals(2, logic.getLessonList().size());
        assertEquals(4, logic.getAllLessons().size());
    }

    @Test
    public void listViewProperty_followsTheModel() {
        assertEquals(ListView.STUDENTS, logic.listViewProperty().get());

        model.setListView(ListView.LESSONS);

        assertEquals(ListView.LESSONS, logic.listViewProperty().get());
    }

    @Test
    public void getListsAreUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getStudentList().remove(0));
        assertThrows(UnsupportedOperationException.class, () -> logic.getLessonList().remove(0));
    }

    @Test
    public void guiSettings_goToTheModel() {
        seedu.address.commons.core.GuiSettings settings = new seedu.address.commons.core.GuiSettings(1, 2, 3, 4);

        logic.setGuiSettings(settings);

        assertEquals(settings, logic.getGuiSettings());
        assertEquals(settings, model.getGuiSettings());
    }

    private void assertParseException(String inputCommand, String expectedMessage) {
        assertThrows(ParseException.class, expectedMessage, () -> logic.execute(inputCommand));
    }

    private Logic createLogicWithFailingStorage() {
        JsonTuiTrackerStorage failingStorage = new JsonTuiTrackerStorage(temporaryFolder.resolve("failing.json")) {
            @Override
            public void saveTuiTracker(ReadOnlyTuiTracker tuiTracker, Path filePath) throws IOException {
                throw DUMMY_IO_EXCEPTION;
            }
        };
        return new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
    }

    /**
     * Returns a {@code Logic} whose only command adds a student, and whose storage always fails with
     * {@code exception}. If {@code saveFailureMessage} is not null, the command supplies that message for a failed
     * save.
     */
    private Logic logicThatChangesDataThenFailsToSave(IOException exception, String saveFailureMessage) {
        JsonTuiTrackerStorage failingStorage = new JsonTuiTrackerStorage(temporaryFolder.resolve("failing.json")) {
            @Override
            public void saveTuiTracker(ReadOnlyTuiTracker tuiTracker, Path filePath) throws IOException {
                throw exception;
            }
        };
        StorageManager storage = new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));

        AddressBookParser parserWithAChangingCommand = new AddressBookParser() {
            @Override
            public Command parseCommand(String userInput) {
                return new Command() {
                    @Override
                    public CommandResult execute(Model model) {
                        Student added = model.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
                        return new CommandResult("Added " + added.getName());
                    }

                    @Override
                    public String getSaveFailureMessage(String defaultMessage) {
                        return saveFailureMessage == null ? defaultMessage : saveFailureMessage;
                    }
                };
            }
        };
        return new LogicManager(model, storage, parserWithAChangingCommand);
    }
}
