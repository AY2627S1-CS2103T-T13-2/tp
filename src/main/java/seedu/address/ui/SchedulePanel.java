package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import seedu.address.model.student.StudentId;

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
    private static final double PAGE_SCROLL_FRACTION = 0.8;

    private final Logger logger = LogsCenter.getLogger(SchedulePanel.class);
    private final Logic logic;
    private final Runnable focusCommandBox;
    private List<DayColumn> days = List.of();

    private record DayColumn(VBox root, Button heading, ScrollPane scroll, List<Button> lessons) { }

    @FXML
    private HBox weekColumns;

    @FXML
    private ScrollPane weekScroll;

    @FXML
    private Label subtitle;

    @FXML
    private Label warningLabel;

    /** Creates a schedule panel backed by the current lesson and enrollment data. */
    public SchedulePanel(Logic logic, Runnable focusCommandBox) {
        super(FXML);
        this.logic = requireNonNull(logic);
        this.focusCommandBox = requireNonNull(focusCommandBox);
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        refresh();
    }

    /** Rebuilds each weekday column after a command may have changed lessons or enrollments. */
    public void refresh() {
        Optional<StudentId> selectedStudent = logic.getScheduleStudentId();
        List<Lesson> lessons = logic.getAllLessons();
        if (selectedStudent.isPresent()) {
            Set<LessonId> enrolledLessonIds = Set.copyOf(logic.getLessonIdsOf(selectedStudent.get()));
            lessons = lessons.stream().filter(lesson -> enrolledLessonIds.contains(lesson.getId())).toList();
        }
        subtitle.setText(selectedStudent.map(id -> "Lessons for student ID " + id
                        + ", Monday to Sunday · Select a bar or marker for details")
                .orElse("Recurring lessons, Monday to Sunday · Select a bar or marker for details"));

        Map<DayOfWeek, List<Lesson>> byDay = ScheduleLayout.groupByDay(lessons);
        Set<LessonId> overlapping = ScheduleLayout.overlappingLessonIds(byDay);

        List<String> clashingDays = byDay.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(lesson -> overlapping.contains(lesson.getId())))
                .map(entry -> Timeslot.dayName(entry.getKey()))
                .collect(Collectors.toList());

        List<DayColumn> dayColumns = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            dayColumns.add(createDayColumn(day, byDay.get(day), overlapping));
        }
        days = List.copyOf(dayColumns);
        weekColumns.getChildren().setAll(days.stream().map(DayColumn::root).toList());

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

    /** Focuses Monday so the schedule can be explored without a mouse. */
    public void focusFirstDay() {
        assert days.size() == DayOfWeek.values().length : "The schedule must contain every weekday";
        focusDay(0);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            focusCommandBox.run();
            event.consume();
            return;
        }

        if (getRoot().getScene() == null) {
            return;
        }
        Node focusOwner = getRoot().getScene().getFocusOwner();
        int dayIndex = dayIndexOf(focusOwner);
        if (dayIndex < 0) {
            return;
        }

        DayColumn day = days.get(dayIndex);
        switch (event.getCode()) {
            case LEFT:
                focusDay(Math.max(0, dayIndex - 1));
                event.consume();
                break;
            case RIGHT:
                focusDay(Math.min(days.size() - 1, dayIndex + 1));
                event.consume();
                break;
            case UP:
                focusAdjacentLesson(day, focusOwner, -1);
                event.consume();
                break;
            case DOWN:
                focusAdjacentLesson(day, focusOwner, 1);
                event.consume();
                break;
            case PAGE_UP:
                scrollDayBy(day.scroll(), -day.scroll().getViewportBounds().getHeight() * PAGE_SCROLL_FRACTION);
                event.consume();
                break;
            case PAGE_DOWN:
                scrollDayBy(day.scroll(), day.scroll().getViewportBounds().getHeight() * PAGE_SCROLL_FRACTION);
                event.consume();
                break;
            case HOME:
                day.scroll().setVvalue(0);
                event.consume();
                break;
            case END:
                day.scroll().setVvalue(1);
                event.consume();
                break;
            default:
                break;
        }
    }

    private int dayIndexOf(Node focused) {
        for (int index = 0; index < days.size(); index++) {
            for (Node node = focused; node != null; node = node.getParent()) {
                if (node == days.get(index).root()) {
                    return index;
                }
            }
        }
        return -1;
    }

    private void focusDay(int index) {
        DayColumn day = days.get(index);
        day.heading().requestFocus();
        scrollDayIntoView(day.root());
    }

    private void focusAdjacentLesson(DayColumn day, Node focused, int direction) {
        if (day.lessons().isEmpty()) {
            scrollDayBy(day.scroll(), direction * PIXELS_PER_HOUR);
            return;
        }
        int currentIndex = day.lessons().indexOf(focused);
        int nextIndex = currentIndex < 0
                ? (direction > 0 ? 0 : day.lessons().size() - 1)
                : currentIndex + direction;
        if (nextIndex >= 0 && nextIndex < day.lessons().size()) {
            focusLesson(day, nextIndex);
        }
    }

    private void focusLesson(DayColumn day, int index) {
        Button lesson = day.lessons().get(index);
        lesson.requestFocus();
        ScrollPane scroll = day.scroll();
        double viewportHeight = scroll.getViewportBounds().getHeight();
        double scrollableHeight = scrollableHeight(scroll);
        if (scrollableHeight <= 0 || viewportHeight <= 0) {
            return;
        }
        double top = scroll.getVvalue() * scrollableHeight;
        double lessonTop = lesson.getLayoutY();
        double lessonBottom = lessonTop + lesson.getPrefHeight();
        if (lessonTop < top) {
            scroll.setVvalue(lessonTop / scrollableHeight);
        } else if (lessonBottom > top + viewportHeight) {
            scroll.setVvalue(Math.min(1, (lessonBottom - viewportHeight) / scrollableHeight));
        }
    }

    private void scrollDayIntoView(VBox day) {
        double viewportWidth = weekScroll.getViewportBounds().getWidth();
        double scrollableWidth = Math.max(0, weekColumns.getBoundsInLocal().getWidth() - viewportWidth);
        if (scrollableWidth <= 0 || viewportWidth <= 0) {
            return;
        }
        double left = weekScroll.getHvalue() * scrollableWidth;
        double dayLeft = day.getLayoutX();
        double dayRight = dayLeft + day.getWidth();
        if (dayLeft < left) {
            weekScroll.setHvalue(dayLeft / scrollableWidth);
        } else if (dayRight > left + viewportWidth) {
            weekScroll.setHvalue(Math.min(1, (dayRight - viewportWidth) / scrollableWidth));
        }
    }

    private void scrollDayBy(ScrollPane scroll, double pixels) {
        double scrollableHeight = scrollableHeight(scroll);
        if (scrollableHeight > 0) {
            double nextValue = scroll.getVvalue() + pixels / scrollableHeight;
            scroll.setVvalue(Math.max(0, Math.min(1, nextValue)));
        }
    }

    private double scrollableHeight(ScrollPane scroll) {
        return Math.max(0, scroll.getContent().getBoundsInLocal().getHeight()
                - scroll.getViewportBounds().getHeight());
    }

    private DayColumn createDayColumn(DayOfWeek day, List<Lesson> lessons, Set<LessonId> overlapping) {
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

        Button heading = new Button(Timeslot.dayName(day));
        heading.getStyleClass().add("schedule-day-heading");
        heading.setAccessibleText(Timeslot.dayName(day) + ", " + lessons.size() + " lessons");
        List<Button> buttons = new ArrayList<>();
        Pane timeline = createTimeline(groups, overlapping, buttons);

        if (lessons.isEmpty()) {
            Label empty = new Label("No lessons");
            empty.getStyleClass().add("schedule-empty");
            empty.relocate(RULER_WIDTH + CARD_GAP, CARD_GAP);
            timeline.getChildren().add(empty);
        }

        ScrollPane dayScroll = createDayScroll(timeline, TIMELINE_WIDTH);
        column.getChildren().addAll(heading, dayScroll);
        DayColumn dayColumn = new DayColumn(column, heading, dayScroll, List.copyOf(buttons));
        heading.setOnAction(event -> {
            if (buttons.isEmpty()) {
                dayScroll.requestFocus();
            } else {
                focusLesson(dayColumn, 0);
            }
        });
        return dayColumn;
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

    private Pane createTimeline(List<ScheduleLayout.DisplayGroup> groups, Set<LessonId> overlapping,
            List<Button> buttons) {
        double dayHeight = HOURS_PER_DAY * PIXELS_PER_HOUR;
        double height = dayHeight + END_OF_DAY_LABEL_SPACE;
        Pane timeline = new Pane();
        timeline.getStyleClass().add("schedule-timeline");
        timeline.setPrefSize(TIMELINE_WIDTH, height);
        timeline.setMinSize(TIMELINE_WIDTH, height);

        addHourMarkers(timeline, dayHeight);
        addLessonGroups(timeline, groups, overlapping, buttons);
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
            Set<LessonId> overlapping, List<Button> buttons) {
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
            buttons.add(button);
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
        button.setOnAction(event -> {
            showLessonDetails(group);
            button.requestFocus();
        });
        double buttonHeight = isShort ? SHORT_MARKER_LABEL_HEIGHT : height;
        double buttonWidth = CARD_WIDTH - (isShort ? 7 : 0);
        button.setPrefSize(buttonWidth, buttonHeight);
        button.setMinSize(buttonWidth, buttonHeight);
        button.setMaxSize(buttonWidth, buttonHeight);
        return button;
    }

    private void showLessonDetails(ScheduleLayout.DisplayGroup group) {
        List<Lesson> lessons = group.lessons();
        assert !lessons.isEmpty() : "A displayed group must contain at least one lesson";
        VBox cards = new VBox(10);
        cards.setPadding(new Insets(4));
        for (Lesson lesson : lessons) {
            cards.getChildren().add(createDetailCard(lesson));
        }

        ScrollPane detailsScroll = new ScrollPane(cards);
        detailsScroll.getStyleClass().add("schedule-details-scroll");
        detailsScroll.setFitToWidth(true);
        detailsScroll.setFocusTraversable(true);
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
        dialogPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                dialog.close();
                event.consume();
            }
        });

        Label heading = new Label(lessons.size() + (lessons.size() == 1 ? " lesson" : " lessons")
                + " · " + Timeslot.dayName(lessons.get(0).getTimeslot().getDay()) + " "
                + timeText(group.startMinute()) + "–" + timeText(group.endMinute()));
        heading.getStyleClass().add("schedule-details-heading");
        VBox content = new VBox(10, heading, detailsScroll);
        content.getStyleClass().add("schedule-details-content");
        dialogPane.setContent(content);
        dialog.setOnShown(event -> detailsScroll.requestFocus());
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
        card.setFocusTraversable(true);
        card.setAccessibleText(title.getText() + ", " + time.getText() + ", " + idAndFee.getText()
                + ", " + attendees.getText());
        return card;
    }
}
