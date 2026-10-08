package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalClock.CLOCK;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalStudents.DANIEL;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
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
    public void execute_commandThatSucceeds_savesTheData() throws Exception {
        CommandResult result = logic.execute("help");

        assertEquals(HelpCommand.SHOWING_HELP_MESSAGE, result.getFeedbackToUser());
        assertTrue(temporaryFolder.resolve("tuitracker.json").toFile().exists());
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
