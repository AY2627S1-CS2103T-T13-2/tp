package seedu.address.ui;

import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.Student;

/**
 * A UI component that displays information of a {@code Student}.
 * This is a basic version: the owner of {@code list} and the UI shapes it further.
 */
public class StudentCard extends UiPart<Region> {

    private static final String FXML = "StudentListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Student student;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label contact;
    @FXML
    private Label level;
    @FXML
    private Label payment;
    @FXML
    private Label lessons;

    /**
     * Creates a {@code StudentCard} with the given {@code Student}, whether they have paid this month, and the IDs
     * of the lessons they are enrolled in.
     */
    public StudentCard(Student student, boolean paid, List<LessonId> lessonIds) {
        super(FXML);
        this.student = student;
        name.setText(student.getName().fullName);
        id.setText("Student ID: " + student.getId());
        contact.setText("Contact: " + student.getContact().value);
        level.setText("Level: " + student.getLevel());
        payment.setText("Payment: " + (paid ? "Paid" : "Unpaid"));
        lessons.setText("Lessons: " + (lessonIds.isEmpty() ? "none"
                : lessonIds.stream().map(LessonId::toString).collect(Collectors.joining(", "))));
    }
}
