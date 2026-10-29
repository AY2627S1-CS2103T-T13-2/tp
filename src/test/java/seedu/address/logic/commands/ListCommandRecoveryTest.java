package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonTuiTrackerStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Checks List feedback while the app is recovering from a data-loading failure. */
public class ListCommandRecoveryTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_inRecovery_showsEmptyListsAndPreservesUnreadableFile() throws Exception {
        Path dataFile = temporaryFolder.resolve("tuitracker.json");
        String unreadableData = "{invalid JSON";
        Files.writeString(dataFile, unreadableData);
        Model model = new ModelManager();
        Logic logic = new LogicManager(model, new StorageManager(new JsonTuiTrackerStorage(dataFile),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))), true);

        Map<String, ListView> requests = Map.of("list", ListView.STUDENTS,
                "list students", ListView.STUDENTS, "list lessons", ListView.LESSONS);
        for (Map.Entry<String, ListView> request : requests.entrySet()) {
            CommandResult result = logic.execute(request.getKey());

            String expectedMessage = request.getValue() == ListView.STUDENTS
                    ? "No students found." : "No lessons found.";
            assertEquals(expectedMessage, result.getFeedbackToUser());
            assertEquals(request.getValue(), model.getListView());
            assertEquals(unreadableData, Files.readString(dataFile));
        }

        CommandException exception = assertThrows(CommandException.class, () ->
                logic.execute("add s/Nora Lim c/90000000 slv/Primary 6"));
        assertEquals(LogicManager.DATA_FILE_UNREADABLE_MESSAGE, exception.getMessage());
        assertEquals(unreadableData, Files.readString(dataFile));
        assertEquals(0, model.getTuiTracker().getStudentList().size());
        assertEquals(0, model.getTuiTracker().getLessonList().size());
    }
}
