package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalTuiTracker.getTypicalTuiTracker;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.TuiTracker;
import seedu.address.model.enrollment.EnrollmentList;
import seedu.address.model.lesson.UniqueLessonList;
import seedu.address.model.student.Name;
import seedu.address.model.student.UniqueStudentList;

public class JsonSerializableTuiTrackerTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableTuiTrackerTest");

    private static JsonSerializableTuiTracker read(String fileName) throws Exception {
        return JsonUtil.readJsonFile(TEST_DATA_FOLDER.resolve(fileName), JsonSerializableTuiTracker.class).get();
    }

    private static String inconsistent(String reason) {
        return String.format(JsonSerializableTuiTracker.MESSAGE_INCONSISTENT_DATA, reason);
    }

    @Test
    public void toModelType_typicalFile_success() throws Exception {
        TuiTracker fromFile = read("typicalTuiTracker.json").toModelType();

        assertEquals(getTypicalTuiTracker(), fromFile);
    }

    @Test
    public void toModelType_roundTrip_keepsEverything() throws Exception {
        TuiTracker original = getTypicalTuiTracker();

        TuiTracker restored = new JsonSerializableTuiTracker(original).toModelType();

        assertEquals(original, restored);
    }

    @Test
    public void toModelType_roundTrip_keepsTheIdCountersEvenAfterTheNewestRecordsAreDeleted() throws Exception {
        TuiTracker original = getTypicalTuiTracker();
        original.deleteLesson(seedu.address.testutil.TypicalLessons.ENGLISH.getId());

        TuiTracker restored = new JsonSerializableTuiTracker(original).toModelType();

        assertEquals(original.getNextLessonId(), restored.getNextLessonId()); // the deleted ID is not reused
        assertEquals(6, restored.getNextLessonId());
    }

    @Test
    public void toModelType_duplicateStudentIds_throwsIllegalValueException() throws Exception {
        JsonSerializableTuiTracker data = read("duplicateStudentIds.json");
        assertThrows(IllegalValueException.class, inconsistent(UniqueStudentList.MESSAGE_DUPLICATE_ID),
                data::toModelType);
    }

    @Test
    public void toModelType_duplicateLessonIds_throwsIllegalValueException() throws Exception {
        JsonSerializableTuiTracker data = read("duplicateLessonIds.json");
        assertThrows(IllegalValueException.class, inconsistent(UniqueLessonList.MESSAGE_DUPLICATE_ID),
                data::toModelType);
    }

    @Test
    public void toModelType_enrollmentOfAMissingRecord_throwsIllegalValueException() throws Exception {
        JsonSerializableTuiTracker data = read("danglingEnrollment.json");
        assertThrows(IllegalValueException.class, inconsistent(TuiTracker.MESSAGE_DANGLING_ENROLLMENT),
                data::toModelType);
    }

    @Test
    public void toModelType_sameEnrollmentTwice_throwsIllegalValueException() throws Exception {
        JsonSerializableTuiTracker data = read("duplicateEnrollment.json");
        assertThrows(IllegalValueException.class, inconsistent(EnrollmentList.MESSAGE_DUPLICATE_ENROLLMENT),
                data::toModelType);
    }

    @Test
    public void toModelType_invalidStudent_throwsIllegalValueException() throws Exception {
        JsonSerializableTuiTracker data = read("invalidStudent.json");
        assertThrows(IllegalValueException.class, Name.MESSAGE_CONSTRAINTS, data::toModelType);
    }

    @Test
    public void toModelType_duplicateStudentsWithDifferentIds_areKeptSeparately() throws Exception {
        // add rejects such a student, but a hand-edited file may contain two, and they are listed as they are
        TuiTracker fromFile = read("duplicateStudentsWithDifferentIds.json").toModelType();

        assertEquals(2, fromFile.getStudentList().size());
        assertTrue(fromFile.getStudentList().get(0).isSameStudent(fromFile.getStudentList().get(1)));
    }

    @Test
    public void toModelType_countersMissing_areWorkedOutFromTheHighestIds() throws Exception {
        TuiTracker fromFile = read("countersMissing.json").toModelType();

        assertEquals(8, fromFile.getNextStudentId());
        assertEquals(4, fromFile.getNextLessonId());
    }

    @Test
    public void toModelType_countersTooLow_areRaisedSoNoIdIsReused() throws Exception {
        TuiTracker fromFile = read("countersTooLow.json").toModelType();

        assertEquals(8, fromFile.getNextStudentId());
        assertEquals(4, fromFile.getNextLessonId());
    }

    @Test
    public void toModelType_emptyDocument_isAnEmptyTuiTracker() throws Exception {
        JsonSerializableTuiTracker empty = new JsonSerializableTuiTracker(null, null, null, null, null);

        assertEquals(new TuiTracker(), empty.toModelType());
    }
}
