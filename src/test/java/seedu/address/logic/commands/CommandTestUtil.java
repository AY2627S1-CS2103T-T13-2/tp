package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.TuiTracker;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - nothing in the model changed: not the data, the lists being shown, or the view
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        TuiTracker dataBefore = new TuiTracker(actualModel.getTuiTracker());
        List<Student> studentsBefore = new ArrayList<>(actualModel.getStudentList());
        List<Lesson> lessonsBefore = new ArrayList<>(actualModel.getLessonList());
        ListView viewBefore = actualModel.getListView();

        CommandException thrown = assertThrows(CommandException.class, () -> command.execute(actualModel));

        assertEquals(expectedMessage, thrown.getMessage());
        assertEquals(dataBefore, actualModel.getTuiTracker());
        assertEquals(studentsBefore, actualModel.getStudentList());
        assertEquals(lessonsBefore, actualModel.getLessonList());
        assertEquals(viewBefore, actualModel.getListView());
    }

}
