package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalLessons.HISTORY;
import static seedu.address.testutil.TypicalStudents.DANIEL;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyTuiTracker;
import seedu.address.model.TuiTracker;

public class JsonTuiTrackerStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonTuiTrackerStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readTuiTracker_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readTuiTracker(null));
    }

    private Optional<ReadOnlyTuiTracker> readTuiTracker(String filePath) throws Exception {
        return new JsonTuiTrackerStorage(Paths.get(filePath)).readTuiTracker(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readTuiTracker("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readTuiTracker("notJsonFormatTuiTracker.json"));
    }

    @Test
    public void readTuiTracker_invalidStudentTuiTracker_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readTuiTracker("invalidStudentTuiTracker.json"));
    }

    @Test
    public void readTuiTracker_invalidAndValidStudentTuiTracker_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readTuiTracker("invalidAndValidStudentTuiTracker.json"));
    }

    @Test
    public void readAndSaveTuiTracker_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempTuiTracker.json");
        TuiTracker original = getTypicalTuiTracker();
        JsonTuiTrackerStorage jsonTuiTrackerStorage = new JsonTuiTrackerStorage(filePath);

        // Save in new file and read back
        jsonTuiTrackerStorage.saveTuiTracker(original, filePath);
        ReadOnlyTuiTracker readBack = jsonTuiTrackerStorage.readTuiTracker(filePath).get();
        assertEquals(original, new TuiTracker(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
        original.addLesson(HISTORY.getSubject(), HISTORY.getLevel(), HISTORY.getTimeslot(), HISTORY.getFee());
        original.deleteStudent(seedu.address.testutil.TypicalStudents.ALICE.getId());
        jsonTuiTrackerStorage.saveTuiTracker(original, filePath);
        readBack = jsonTuiTrackerStorage.readTuiTracker(filePath).get();
        assertEquals(original, new TuiTracker(readBack));

        // Save and read without specifying file path
        original.addStudent(DANIEL.getName(), DANIEL.getContact(), DANIEL.getLevel());
        jsonTuiTrackerStorage.saveTuiTracker(original); // file path not specified
        readBack = jsonTuiTrackerStorage.readTuiTracker().get(); // file path not specified
        assertEquals(original, new TuiTracker(readBack));
    }

    @Test
    public void saveTuiTracker_nullTuiTracker_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveTuiTracker(null, "SomeFile.json"));
    }

    @Test
    public void saveTuiTracker_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveTuiTracker(new TuiTracker(), null));
    }

    private void saveTuiTracker(ReadOnlyTuiTracker tuiTracker, String filePath) throws IOException {
        new JsonTuiTrackerStorage(Paths.get(filePath))
                .saveTuiTracker(tuiTracker, addToTestDataPathIfNotNull(filePath));
    }

    @Test
    public void getTuiTrackerFilePath() {
        Path path = testFolder.resolve("any.json");
        assertEquals(path, new JsonTuiTrackerStorage(path).getTuiTrackerFilePath());
    }
}
