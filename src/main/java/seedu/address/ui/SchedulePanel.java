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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    private static final int MINUTES_PER_HOUR = 60;
    private static final int MINUTES_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR;
    private static final double BASE_PIXELS_PER_MINUTE = 2.5;
    private static final double MIN_CARD_HEIGHT = 140;
    private static final double RULER_WIDTH = 50;
    private static final double CARD_WIDTH = 220;
    private static final double CARD_GAP = 8;
    private static final double CARD_VERTICAL_INSET = 2;
    private static final double DAY_CHROME_WIDTH = 36;
    private static final double PREFERRED_DAY_VIEWPORT_HEIGHT = 600;

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
        double pixelsPerMinute = pixelsPerMinute(byDay);

        List<String> clashingDays = byDay.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(lesson -> overlapping.contains(lesson.getId())))
                .map(entry -> Timeslot.dayName(entry.getKey()))
                .collect(Collectors.toList());

        List<VBox> dayColumns = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            dayColumns.add(createDayColumn(day, byDay.get(day), overlapping, pixelsPerMinute));
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

    /** Gives even the shortest lesson enough height to show its details. */
    private double pixelsPerMinute(Map<DayOfWeek, List<Lesson>> byDay) {
        double scale = BASE_PIXELS_PER_MINUTE;
        for (List<Lesson> lessons : byDay.values()) {
            for (Lesson lesson : lessons) {
                Timeslot timeslot = lesson.getTimeslot();
                double durationMinutes = minutesOfDay(timeslot.getEnd()) - minutesOfDay(timeslot.getStart());
                scale = Math.max(scale, (MIN_CARD_HEIGHT + 2 * CARD_VERTICAL_INSET) / durationMinutes);
            }
        }
        return scale;
    }

    private VBox createDayColumn(DayOfWeek day, List<Lesson> lessons, Set<LessonId> overlapping,
            double pixelsPerMinute) {
        assert lessons.stream().allMatch(lesson -> lesson.getTimeslot().getDay() == day)
                : "Lessons must be grouped by day";
        List<ScheduleLayout.PlacedLesson> placements = ScheduleLayout.placeInLanes(lessons);
        int laneCount = placements.stream().mapToInt(placement -> placement.lane() + 1).max().orElse(1);
        double timelineWidth = RULER_WIDTH + CARD_GAP + laneCount * (CARD_WIDTH + CARD_GAP);

        VBox column = new VBox(8);
        column.getStyleClass().add("schedule-day");
        if (day == LocalDate.now().getDayOfWeek()) {
            column.getStyleClass().add("schedule-today");
        }
        column.setMinWidth(timelineWidth + DAY_CHROME_WIDTH);
        column.setPrefWidth(timelineWidth + DAY_CHROME_WIDTH);
        column.setMaxWidth(timelineWidth + DAY_CHROME_WIDTH);

        Label heading = new Label(Timeslot.dayName(day));
        heading.getStyleClass().add("schedule-day-heading");
        Pane timeline = createTimeline(placements, overlapping, timelineWidth, pixelsPerMinute);

        if (lessons.isEmpty()) {
            Label empty = new Label("No lessons");
            empty.getStyleClass().add("schedule-empty");
            empty.relocate(RULER_WIDTH + CARD_GAP, CARD_GAP);
            timeline.getChildren().add(empty);
        }

        ScrollPane dayScroll = createDayScroll(timeline, timelineWidth);
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

    private Pane createTimeline(List<ScheduleLayout.PlacedLesson> placements, Set<LessonId> overlapping,
            double width, double pixelsPerMinute) {
        double height = MINUTES_PER_DAY * pixelsPerMinute;
        Pane timeline = new Pane();
        timeline.getStyleClass().add("schedule-timeline");
        timeline.setPrefSize(width, height);
        timeline.setMinSize(width, height);

        addHourMarkers(timeline, width, height, pixelsPerMinute);
        addLessonCards(timeline, placements, overlapping, pixelsPerMinute);
        return timeline;
    }

    private void addHourMarkers(Pane timeline, double width, double height, double pixelsPerMinute) {
        for (int hour = 0; hour <= HOURS_PER_DAY; hour++) {
            double y = hour * MINUTES_PER_HOUR * pixelsPerMinute;
            Region line = new Region();
            line.getStyleClass().add("schedule-hour-line");
            line.setPrefSize(width - RULER_WIDTH, 1);
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
        endOfDay.relocate(4, height - 19);
        timeline.getChildren().add(endOfDay);
    }

    private void addLessonCards(Pane timeline, List<ScheduleLayout.PlacedLesson> placements,
            Set<LessonId> overlapping, double pixelsPerMinute) {
        for (ScheduleLayout.PlacedLesson placement : placements) {
            Lesson lesson = placement.lesson();
            Timeslot timeslot = lesson.getTimeslot();
            double x = RULER_WIDTH + CARD_GAP + placement.lane() * (CARD_WIDTH + CARD_GAP);
            double y = minutesOfDay(timeslot.getStart()) * pixelsPerMinute + CARD_VERTICAL_INSET;
            double cardHeight = (minutesOfDay(timeslot.getEnd()) - minutesOfDay(timeslot.getStart()))
                    * pixelsPerMinute - 2 * CARD_VERTICAL_INSET;
            ScrollPane card = createLessonCard(lesson, overlapping.contains(lesson.getId()), cardHeight);
            card.relocate(x, y);
            timeline.getChildren().add(card);
        }
    }

    private static double minutesOfDay(LocalTime time) {
        return time.toSecondOfDay() / 60.0;
    }

    private ScrollPane createLessonCard(Lesson lesson, boolean overlaps, double height) {
        VBox content = new VBox(4);
        content.getStyleClass().add("schedule-lesson-content");

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

        content.getChildren().addAll(time, subject, id, attendees);
        ScrollPane card = new ScrollPane(content);
        card.getStyleClass().add("schedule-lesson");
        if (overlaps) {
            card.getStyleClass().add("schedule-overlap");
        }
        card.setFitToWidth(true);
        card.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        card.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        card.setPrefSize(CARD_WIDTH, height);
        card.setMinSize(CARD_WIDTH, height);
        card.setMaxSize(CARD_WIDTH, height);
        return card;
    }
}
