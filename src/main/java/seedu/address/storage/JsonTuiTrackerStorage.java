package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyTuiTracker;

/**
 * A class to access TuiTracker data stored as a JSON file on the hard disk.
 */
public class JsonTuiTrackerStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonTuiTrackerStorage.class);

    private Path filePath;

    public JsonTuiTrackerStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getTuiTrackerFilePath() {
        return filePath;
    }

    /**
     * Returns TuiTracker data as a {@link ReadOnlyTuiTracker}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyTuiTracker> readTuiTracker() throws DataLoadingException {
        return readTuiTracker(filePath);
    }

    /**
     * Similar to {@link #readTuiTracker()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyTuiTracker> readTuiTracker(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableTuiTracker> jsonTuiTracker = JsonUtil.readJsonFile(
                filePath, JsonSerializableTuiTracker.class);
        if (!jsonTuiTracker.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonTuiTracker.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /**
     * Saves the given {@link ReadOnlyTuiTracker} to the storage.
     * @param tuiTracker cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveTuiTracker(ReadOnlyTuiTracker tuiTracker) throws IOException {
        saveTuiTracker(tuiTracker, filePath);
    }

    /**
     * Similar to {@link #saveTuiTracker(ReadOnlyTuiTracker)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveTuiTracker(ReadOnlyTuiTracker tuiTracker, Path filePath) throws IOException {
        requireNonNull(tuiTracker);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableTuiTracker(tuiTracker), filePath);
    }

}
