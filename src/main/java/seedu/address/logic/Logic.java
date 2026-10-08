package seedu.address.logic;

import java.util.List;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ListView;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /** Returns an unmodifiable view of the students to show, with any filter applied. */
    ObservableList<Student> getStudentList();

    /** Returns an unmodifiable view of the lessons to show, with any filter applied. */
    ObservableList<Lesson> getLessonList();

    /** Returns which list the main panel should show. Observe it to react when {@code list} switches it. */
    ReadOnlyObjectProperty<ListView> listViewProperty();

    /** Returns the IDs of the lessons the student is enrolled in, in ascending order. */
    List<LessonId> getLessonIdsOf(StudentId studentId);

    /** Returns the students enrolled in the lesson, in ascending order of ID. */
    List<Student> getStudentsIn(LessonId lessonId);

    /** Returns true if the student has been marked as paid for the current month. */
    boolean isPaid(StudentId studentId);

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
