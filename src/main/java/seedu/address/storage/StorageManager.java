package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyTuiTracker;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of TuiTracker data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private JsonTuiTrackerStorage tuiTrackerStorage;
    private JsonUserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given TuiTracker and user prefs storage.
     */
    public StorageManager(JsonTuiTrackerStorage tuiTrackerStorage, JsonUserPrefsStorage userPrefsStorage) {
        this.tuiTrackerStorage = tuiTrackerStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ TuiTracker methods ==============================

    @Override
    public Path getTuiTrackerFilePath() {
        return tuiTrackerStorage.getTuiTrackerFilePath();
    }

    @Override
    public Optional<ReadOnlyTuiTracker> readTuiTracker() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + tuiTrackerStorage.getTuiTrackerFilePath());
        return tuiTrackerStorage.readTuiTracker();
    }

    @Override
    public void saveTuiTracker(ReadOnlyTuiTracker tuiTracker) throws IOException {
        logger.fine("Attempting to write to data file: " + tuiTrackerStorage.getTuiTrackerFilePath());
        tuiTrackerStorage.saveTuiTracker(tuiTracker);
    }

}
