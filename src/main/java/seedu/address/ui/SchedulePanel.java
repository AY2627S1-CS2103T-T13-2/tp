package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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
    private static final int HOURS_PER_DAY = 24;
    private static final double PIXELS_PER_HOUR = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int MINIMUM_BAR_MINUTES = 24;
    private static final int SHORT_MARKER_LABEL_MINUTES = 20;
    private static final double MINIMUM_LABEL_HEIGHT = 24;
    private static final double SHORT_MARKER_LABEL_HEIGHT = 20;
    private static final double TWO_LINE_HEIGHT = 40;
    private static final double THREE_LINE_HEIGHT = 64;
    private static final double RULER_WIDTH = 50;
    private static final double CARD_WIDTH = 220;
    private static final double CARD_GAP = 8;
    private static final double END_OF_DAY_LABEL_SPACE = MINIMUM_LABEL_HEIGHT;
    private static final double DAY_CHROME_WIDTH = 36;
    private static final double PREFERRED_DAY_VIEWPORT_HEIGHT = 600;
    private static final double TIMELINE_WIDTH = RULER_WIDTH + CARD_GAP + CARD_WIDTH + CARD_GAP;

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
        List<ScheduleLayout.DisplayGroup> groups = ScheduleLayout.groupForDisplay(lessons,
                MINIMUM_BAR_MINUTES, SHORT_MARKER_LABEL_MINUTES);

        VBox column = new VBox(8);
        column.getStyleClass().add("schedule-day");
        if (day == LocalDate.now().getDayOfWeek()) {
            column.getStyleClass().add("schedule-today");
        }
        column.setMinWidth(TIMELINE_WIDTH + DAY_CHROME_WIDTH);
        column.setPrefWidth(TIMELINE_WIDTH + DAY_CHROME_WIDTH);
        column.setMaxWidth(TIMELINE_WIDTH + DAY_CHROME_WIDTH);

        Label heading = new Label(Timeslot.dayName(day));
        heading.getStyleClass().add("schedule-day-heading");
        Pane timeline = createTimeline(groups, overlapping);

        if (lessons.isEmpty()) {
            Label empty = new Label("No lessons");
            empty.getStyleClass().add("schedule-empty");
            empty.relocate(RULER_WIDTH + CARD_GAP, CARD_GAP);
            timeline.getChildren().add(empty);
        }

        ScrollPane dayScroll = createDayScroll(timeline, TIMELINE_WIDTH);
        column.getChildren().addAll(heading, dayScroll);
        return column;
    }

    private ScrollPane createDayScroll(Pane timeline, double width) {
        ScrollPane dayScroll = new ScrollPane(timeline);
        dayScroll.getStyleClass().add("schedule-day-scroll");
        dayScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dayScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dayScroll.setPrefViewportWidth(width);
        dayScroll.setPrefViewportHeight(PREFERRED_DAY_VIEWPORT_HEIGHT);
        dayScroll.setMinHeight(0);
        VBox.setVgrow(dayScroll, Priority.ALWAYS);
        return dayScroll;
    }

    private Pane createTimeline(List<ScheduleLayout.DisplayGroup> groups, Set<LessonId> overlapping) {
        double dayHeight = HOURS_PER_DAY * PIXELS_PER_HOUR;
        double height = dayHeight + END_OF_DAY_LABEL_SPACE;
        Pane timeline = new Pane();
        timeline.getStyleClass().add("schedule-timeline");
        timeline.setPrefSize(TIMELINE_WIDTH, height);
        timeline.setMinSize(TIMELINE_WIDTH, height);

        addHourMarkers(timeline, dayHeight);
        addLessonGroups(timeline, groups, overlapping);
        return timeline;
    }

    private void addHourMarkers(Pane timeline, double dayHeight) {
        for (int hour = 0; hour <= HOURS_PER_DAY; hour++) {
            double y = hour * PIXELS_PER_HOUR;
            Region line = new Region();
            line.getStyleClass().add("schedule-hour-line");
            line.setPrefSize(TIMELINE_WIDTH - RULER_WIDTH, 1);
            line.relocate(RULER_WIDTH, y);
            timeline.getChildren().add(line);

            if (hour < HOURS_PER_DAY) {
                Label time = new Label(Timeslot.timeText(LocalTime.of(hour, 0)));
                time.getStyleClass().add("schedule-hour-label");
                time.relocate(4, y + 3);
                timeline.getChildren().add(time);
            }
        }

        Label endOfDay = new Label("23:59");
        endOfDay.getStyleClass().add("schedule-hour-label");
        endOfDay.relocate(4, dayHeight + 3);
        timeline.getChildren().add(endOfDay);
    }

    private void addLessonGroups(Pane timeline, List<ScheduleLayout.DisplayGroup> groups,
            Set<LessonId> overlapping) {
        for (ScheduleLayout.DisplayGroup group : groups) {
            double startY = pixelsAtMinute(group.startMinute());
            double height = pixelsAtMinute(group.endMinute() - group.startMinute());
            boolean isShort = height < MINIMUM_LABEL_HEIGHT;
            boolean hasClash = group.lessons().stream().anyMatch(lesson -> overlapping.contains(lesson.getId()));
            Button button = createLessonButton(group, hasClash, isShort, height);
            if (isShort) {
                Region span = new Region();
                span.getStyleClass().add("schedule-short-span");
                if (hasClash) {
                    span.getStyleClass().add("schedule-short-span-overlap");
                }
                span.relocate(RULER_WIDTH + CARD_GAP, startY);
                span.setPrefSize(4, Math.max(1, height));
                timeline.getChildren().add(span);
            }
            double labelY = isShort
                    ? startY + (height - SHORT_MARKER_LABEL_HEIGHT) / 2
                    : startY;
            button.relocate(RULER_WIDTH + CARD_GAP + (isShort ? 7 : 0),
                    Math.max(0, Math.min(labelY, HOURS_PER_DAY * PIXELS_PER_HOUR - SHORT_MARKER_LABEL_HEIGHT)));
            timeline.getChildren().add(button);
        }
    }

    private static double pixelsAtMinute(int minute) {
        return minute * PIXELS_PER_HOUR / MINUTES_PER_HOUR;
    }

    private static String timeText(int minute) {
        return Timeslot.timeText(LocalTime.of(minute / MINUTES_PER_HOUR, minute % MINUTES_PER_HOUR));
    }

    private Button createLessonButton(ScheduleLayout.DisplayGroup group, boolean hasClash,
            boolean isShort, double height) {
        List<Lesson> lessons = group.lessons();
        String range = timeText(group.startMinute()) + "–" + timeText(group.endMinute());
        String label;
        if (lessons.size() == 1) {
            Lesson lesson = lessons.get(0);
            label = range + "  " + lesson.getSubject();
            if (!isShort && height >= TWO_LINE_HEIGHT) {
                label = range + "\n" + lesson.getSubject() + " · " + lesson.getLevel();
            }
            if (!isShort && height >= THREE_LINE_HEIGHT) {
                List<Student> students = logic.getStudentsIn(lesson.getId());
                String studentSummary = students.isEmpty() ? "No students"
                        : students.size() == 1 ? students.get(0).getName().toString()
                        : students.size() + " students";
                label += "\n" + studentSummary;
            }
        } else {
            label = height >= TWO_LINE_HEIGHT
                    ? range + "\n" + lessons.size() + " lessons"
                    : range + "  " + lessons.size() + " lessons";
        }

        Button button = new Button(label);
        button.getStyleClass().add("schedule-lesson");
        if (isShort) {
            button.getStyleClass().add("schedule-short-lesson");
        }
        if (hasClash) {
            button.getStyleClass().add(isShort ? "schedule-short-overlap" : "schedule-overlap");
        }
        String description = lessons.size() == 1
                ? "View details for " + lessons.get(0).getSubject() + " at " + range
                : "View details for " + lessons.size() + " lessons between " + range;
        button.setTooltip(new Tooltip(description));
        button.setAccessibleText(description);
        button.setOnAction(event -> showLessonDetails(group));
        double buttonHeight = isShort ? SHORT_MARKER_LABEL_HEIGHT : height;
        double buttonWidth = CARD_WIDTH - (isShort ? 7 : 0);
        button.setPrefSize(buttonWidth, buttonHeight);
        button.setMinSize(buttonWidth, buttonHeight);
        button.setMaxSize(buttonWidth, buttonHeight);
        return button;
    }

    private void showLessonDetails(ScheduleLayout.DisplayGroup group) {
        List<Lesson> lessons = group.lessons();
        VBox cards = new VBox(10);
        cards.setPadding(new Insets(4));
        for (Lesson lesson : lessons) {
            cards.getChildren().add(createDetailCard(lesson));
        }

        ScrollPane detailsScroll = new ScrollPane(cards);
        detailsScroll.getStyleClass().add("schedule-details-scroll");
        detailsScroll.setFitToWidth(true);
        detailsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        detailsScroll.setPrefViewportWidth(460);
        detailsScroll.setPrefViewportHeight(380);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(getRoot().getScene().getWindow());
        dialog.setTitle("Lesson details");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().addAll("view/DarkTheme.css", "view/Extensions.css");
        dialogPane.getStyleClass().add("schedule-details-dialog");
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        Label heading = new Label(lessons.size() + (lessons.size() == 1 ? " lesson" : " lessons")
                + " · " + Timeslot.dayName(lessons.get(0).getTimeslot().getDay()) + " "
                + timeText(group.startMinute()) + "–" + timeText(group.endMinute()));
        heading.getStyleClass().add("schedule-details-heading");
        VBox content = new VBox(10, heading, detailsScroll);
        content.getStyleClass().add("schedule-details-content");
        dialogPane.setContent(content);
        dialog.showAndWait();
    }

    private VBox createDetailCard(Lesson lesson) {
        List<Student> students = logic.getStudentsIn(lesson.getId());
        String studentsText = students.isEmpty()
                ? "No students"
                : students.stream().map(student -> student.getName() + " (ID: " + student.getId() + ")")
                        .collect(Collectors.joining(", "));

        Label title = new Label(lesson.getSubject() + " · " + lesson.getLevel());
        title.getStyleClass().add("schedule-details-card-title");
        Label time = new Label(lesson.getTimeslot().toString());
        Label idAndFee = new Label("Lesson ID: " + lesson.getId() + "    Fee per session: " + lesson.getFee());
        Label attendees = new Label("Students: " + studentsText);
        for (Label label : List.of(time, idAndFee, attendees)) {
            label.getStyleClass().add("schedule-details-card-text");
            label.setWrapText(true);
        }
        title.setWrapText(true);

        VBox card = new VBox(4, title, time, idAndFee, attendees);
        card.getStyleClass().add("schedule-details-card");
        return card;
    }
}
