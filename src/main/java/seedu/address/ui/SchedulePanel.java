package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Student;

/** A seven-column view of the recurring weekly lesson schedule. */
public class SchedulePanel extends UiPart<Region> {

    private static final String FXML = "SchedulePanel.fxml";

    private final Logger logger = LogsCenter.getLogger(SchedulePanel.class);
    private final Logic logic;

    @FXML
    private HBox weekColumns;

    @FXML
    private Label warningLabel;

    /** Creates a schedule panel backed by the current lesson and enrollment data. */
    public SchedulePanel(Logic logic) {
        super(FXML);
        this.logic = requireNonNull(logic);
        refresh();
    }

    /** Rebuilds each weekday column after a command may have changed lessons or enrollments. */
    public void refresh() {
        Map<DayOfWeek, List<Lesson>> byDay = ScheduleLayout.groupByDay(logic.getAllLessons());
        Set<LessonId> overlapping = ScheduleLayout.overlappingLessonIds(byDay);

        List<String> clashingDays = byDay.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(lesson -> overlapping.contains(lesson.getId())))
                .map(entry -> Timeslot.dayName(entry.getKey()))
                .collect(Collectors.toList());

        List<VBox> dayColumns = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            dayColumns.add(createDayColumn(day, byDay.get(day), overlapping));
        }
        weekColumns.getChildren().setAll(dayColumns);

        boolean hasClashes = !clashingDays.isEmpty();
        warningLabel.setVisible(hasClashes);
        warningLabel.setManaged(hasClashes);
        if (hasClashes) {
            String warning = "Warning: Overlapping lessons detected on " + String.join(", ", clashingDays) + ".";
            warningLabel.setText(warning);
            logger.warning(warning);
        } else {
            warningLabel.setText("");
        }
    }

    private VBox createDayColumn(DayOfWeek day, List<Lesson> lessons, Set<LessonId> overlapping) {
        assert lessons.stream().allMatch(lesson -> lesson.getTimeslot().getDay() == day)
                : "Lessons must be grouped by day";
        VBox column = new VBox(8);
        column.getStyleClass().add("schedule-day");
        if (day == LocalDate.now().getDayOfWeek()) {
            column.getStyleClass().add("schedule-today");
        }
        column.setMinWidth(180);
        column.setPrefWidth(190);

        Label heading = new Label(Timeslot.dayName(day));
        heading.getStyleClass().add("schedule-day-heading");
        column.getChildren().add(heading);

        if (lessons.isEmpty()) {
            Label empty = new Label("No lessons");
            empty.getStyleClass().add("schedule-empty");
            column.getChildren().add(empty);
        } else {
            for (Lesson lesson : lessons) {
                column.getChildren().add(createLessonCard(lesson, overlapping.contains(lesson.getId())));
            }
        }
        return column;
    }

    private VBox createLessonCard(Lesson lesson, boolean overlaps) {
        VBox card = new VBox(4);
        card.getStyleClass().add("schedule-lesson");
        if (overlaps) {
            card.getStyleClass().add("schedule-overlap");
        }

        Timeslot timeslot = lesson.getTimeslot();
        Label time = new Label(Timeslot.timeText(timeslot.getStart()) + "–"
                + Timeslot.timeText(timeslot.getEnd()));
        time.getStyleClass().add("schedule-lesson-time");
        Label subject = new Label(lesson.getSubject() + " · " + lesson.getLevel());
        subject.getStyleClass().add("schedule-lesson-subject");
        subject.setWrapText(true);
        Label id = new Label("Lesson ID: " + lesson.getId());
        id.getStyleClass().add("schedule-lesson-detail");

        List<Student> students = logic.getStudentsIn(lesson.getId());
        String studentsText = students.isEmpty()
                ? "No students"
                : students.stream().map(student -> student.getName() + " (ID: " + student.getId() + ")")
                        .collect(Collectors.joining(", "));
        Label attendees = new Label(studentsText);
        attendees.getStyleClass().add("schedule-lesson-detail");
        attendees.setWrapText(true);

        card.getChildren().addAll(time, subject, id, attendees);
        return card;
    }
}
