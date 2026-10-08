package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyTuiTracker;
import seedu.address.model.TuiTracker;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT =
            "Could not save data due to the following error: %s. No changes were made.";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT = "Could not save data to file %s due to "
            + "insufficient permissions to write to the file or the folder. No changes were made.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this(model, storage, new AddressBookParser());
    }

    /**
     * Constructs a {@code LogicManager} that uses the given parser. This lets a test supply a parser that returns
     * a command with known behaviour.
     */
    public LogicManager(Model model, Storage storage, AddressBookParser addressBookParser) {
        this.model = model;
        this.storage = storage;
        this.addressBookParser = addressBookParser;
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = addressBookParser.parseCommand(commandText);

        // Kept so that a command whose changes cannot be saved is undone, leaving the data as it was.
        ReadOnlyTuiTracker before = new TuiTracker(model.getTuiTracker());

        CommandResult commandResult = command.execute(model);

        try {
            storage.saveTuiTracker(model.getTuiTracker());
        } catch (AccessDeniedException e) {
            model.setTuiTracker(before);
            throw new CommandException(command.getSaveFailureMessage(
                    String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage())), e);
        } catch (IOException ioe) {
            model.setTuiTracker(before);
            throw new CommandException(command.getSaveFailureMessage(
                    String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage())), ioe);
        }

        return commandResult;
    }

    @Override
    public ObservableList<Student> getStudentList() {
        return model.getStudentList();
    }

    @Override
    public ObservableList<Lesson> getLessonList() {
        return model.getLessonList();
    }

    @Override
    public ReadOnlyObjectProperty<ListView> listViewProperty() {
        return model.listViewProperty();
    }

    @Override
    public List<LessonId> getLessonIdsOf(StudentId studentId) {
        return model.getLessonIdsOf(studentId);
    }

    @Override
    public List<Student> getStudentsIn(LessonId lessonId) {
        return model.getStudentsIn(lessonId);
    }

    @Override
    public boolean isPaid(StudentId studentId) {
        return model.isPaid(studentId);
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
